package eu.brrm.oblivio.data.remote

import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

class AuthRequestInterceptor @Inject constructor(
    private val sessionMemory: AuthSessionMemory,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.url.encodedPath.contains("/auth/login", ignoreCase = true)) {
            return chain.proceed(request)
        }
        // Public registration: do not attach a previous user session
        if (request.method == "POST" && request.url.encodedPath == "/users") {
            return chain.proceed(request)
        }
        val builder = request.newBuilder()
        val bearer = sessionMemory.bearerToken
        if (!bearer.isNullOrBlank()) {
            builder.header("Authorization", "Bearer $bearer")
        }
        val cookie = sessionMemory.cookieHeader
        if (!cookie.isNullOrBlank()) {
            builder.header("Cookie", cookie)
        }
        return chain.proceed(builder.build())
    }
}
