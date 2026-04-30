package eu.brrm.oblivio.data.remote

import com.squareup.moshi.Moshi
import eu.brrm.oblivio.BuildConfig
import eu.brrm.oblivio.data.local.AuthSessionStore
import eu.brrm.oblivio.data.remote.dto.LoginResponseDto
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.runBlocking
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

@Singleton
class RefreshTokenInterceptor @Inject constructor(
    private val authSessionStore: AuthSessionStore,
    private val sessionMemory: AuthSessionMemory,
    private val logoutNotifier: AuthLogoutNotifier,
    moshi: Moshi,
) : Interceptor {
    private val loginResponseAdapter = moshi.adapter(LoginResponseDto::class.java)

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.code != 401 || request.isAuthRetry() || request.isRefreshRequest()) {
            return response
        }

        val refreshed = refreshAccessToken(chain)
        if (refreshed == null) {
            runBlocking { authSessionStore.clear() }
            sessionMemory.clear()
            logoutNotifier.notifyLoggedOut()
            return response
        }

        response.close()
        val retried = chain.proceed(
            request.newBuilder()
                .header("Authorization", "Bearer ${refreshed.accessToken}")
                .header("Cookie", refreshed.cookieHeader)
                .tag(AuthRetry::class.java, AuthRetry)
                .build(),
        )
        if (retried.code == 401) {
            runBlocking { authSessionStore.clear() }
            sessionMemory.clear()
            logoutNotifier.notifyLoggedOut()
        }
        return retried
    }

    private fun refreshAccessToken(chain: Interceptor.Chain): RefreshResult? {
        val oldAccess = sessionMemory.bearerToken ?: runBlocking { authSessionStore.getAccessToken() }
        val oldCookie = sessionMemory.cookieHeader ?: runBlocking { authSessionStore.getSessionCookieHeader() }
        if (oldAccess.isNullOrBlank() || oldCookie.isNullOrBlank()) return null
        val csrf = extractCookieValue(oldCookie, CSRF_COOKIE_NAME) ?: return null

        repeat(MAX_REFRESH_ATTEMPTS) {
            val refreshResponse = chain.proceed(
                Request.Builder()
                    .url(BuildConfig.API_BASE_URL + REFRESH_PATH)
                    .post(ByteArray(0).toRequestBody(null))
                    .header("Authorization", "Bearer $oldAccess")
                    .header("Cookie", oldCookie)
                    .header("x-csrf", csrf)
                    .tag(AuthRetry::class.java, AuthRetry)
                    .build(),
            )
            refreshResponse.use { response ->
                if (response.isSuccessful) {
                    val bodyString = response.body?.string().orEmpty()
                    val access = runCatching {
                        loginResponseAdapter.fromJson(bodyString)?.accessTokenValue()
                    }.getOrNull()
                    val cookie = buildCookieHeader(response.headers)
                        ?.let { newCookie -> mergeCookieHeaders(oldCookie, newCookie) }
                    if (!access.isNullOrBlank() && !cookie.isNullOrBlank()) {
                        runBlocking {
                            authSessionStore.saveAccessAndCookie(
                                accessToken = access,
                                sessionCookieHeader = cookie,
                            )
                        }
                        sessionMemory.setSession(bearer = access, cookie = cookie)
                        return RefreshResult(accessToken = access, cookieHeader = cookie)
                    }
                }
            }
        }
        return null
    }

    private fun Request.isRefreshRequest(): Boolean =
        url.encodedPath.equals("/$REFRESH_PATH", ignoreCase = true)

    private fun Request.isAuthRetry(): Boolean =
        tag(AuthRetry::class.java) != null

    private fun buildCookieHeader(headers: Headers): String? {
        val parts = headers.values("Set-Cookie")
        if (parts.isEmpty()) return null
        return parts
            .map { it.substringBefore(';').trim() }
            .filter { it.isNotEmpty() }
            .joinToString("; ")
    }

    private fun mergeCookieHeaders(oldCookieHeader: String, newCookieHeader: String): String {
        val cookies = linkedMapOf<String, String>()
        oldCookieHeader.split(';')
            .map { it.trim() }
            .filter { it.contains('=') }
            .forEach { part ->
                cookies[part.substringBefore('=')] = part
            }
        newCookieHeader.split(';')
            .map { it.trim() }
            .filter { it.contains('=') }
            .forEach { part ->
                cookies[part.substringBefore('=')] = part
            }
        return cookies.values.joinToString("; ")
    }

    private fun extractCookieValue(cookieHeader: String, name: String): String? =
        cookieHeader
            .split(';')
            .map { it.trim() }
            .firstOrNull { it.startsWith("$name=") }
            ?.substringAfter('=')
            ?.takeIf { it.isNotBlank() }

    private data class RefreshResult(
        val accessToken: String,
        val cookieHeader: String,
    )

    private object AuthRetry

    private companion object {
        const val MAX_REFRESH_ATTEMPTS = 3
        const val REFRESH_PATH = "auth/refresh"
        const val CSRF_COOKIE_NAME = "csrf_refresh"
    }
}
