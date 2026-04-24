package eu.brrm.oblivio.data.repository

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import eu.brrm.oblivio.data.local.AuthTokenDataSource
import eu.brrm.oblivio.data.remote.ApiErrorMessageParser
import eu.brrm.oblivio.data.remote.AuthApiService
import eu.brrm.oblivio.data.remote.AuthSessionMemory
import eu.brrm.oblivio.data.remote.dto.LoginRequestDto
import eu.brrm.oblivio.data.remote.dto.LoginResponseDto
import eu.brrm.oblivio.domain.ServerErrorException
import eu.brrm.oblivio.domain.model.UserProfile
import eu.brrm.oblivio.domain.repository.AuthRepository
import java.lang.reflect.Type
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val authTokenDataSource: AuthTokenDataSource,
    private val authSessionMemory: AuthSessionMemory,
    private val moshi: Moshi,
) : AuthRepository {
    private val loginResponseAdapter = moshi.adapter(LoginResponseDto::class.java)
    private val registerBodyMapType: Type = Types.newParameterizedType(
        Map::class.java,
        String::class.java,
        String::class.java,
    )
    private val registerMapAdapter: JsonAdapter<Map<String, String>> =
        moshi.adapter(registerBodyMapType)

    override suspend fun signIn(usernameOrEmail: String, password: String): Result<Unit> {
        if (usernameOrEmail.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("empty_credentials"))
        }
        return withContext(Dispatchers.IO) {
            try {
                val response = authApiService.login(
                    LoginRequestDto(
                        identifier = usernameOrEmail.trim(),
                        password = password,
                    ),
                )
                val bodyString = response.body()?.string().orEmpty()
                if (response.isSuccessful) {
                    return@withContext applySessionFromLoginBody(
                        bodyString,
                        response.headers(),
                        usernameOrEmail.trim(),
                    )
                } else {
                    httpFailure(
                        errorBody = response.errorBody()?.use { it.string() },
                        successBody = bodyString,
                        code = response.code(),
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun register(
        username: String,
        password: String,
        email: String?,
    ): Result<Unit> {
        if (username.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("empty_credentials"))
        }
        val trimmed = username.trim()
        return withContext(Dispatchers.IO) {
            try {
                val payload = buildMap {
                    put("username", trimmed)
                    put("password", password)
                    if (!email.isNullOrBlank()) put("email", email.trim())
                }
                val json = registerMapAdapter.toJson(payload)
                val requestBody = json.toRequestBody(JSON_MEDIA)
                val response = authApiService.registerUser(requestBody)
                val bodyString = response.body()?.string().orEmpty()
                if (response.isSuccessful) {
                    return@withContext if (canBuildSessionFromLoginMaterial(bodyString, response.headers())) {
                        applySessionFromLoginBody(bodyString, response.headers(), trimmed)
                    } else {
                        signIn(trimmed, password)
                    }
                } else {
                    httpFailure(
                        errorBody = response.errorBody()?.use { it.string() },
                        successBody = bodyString,
                        code = response.code(),
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun canBuildSessionFromLoginMaterial(body: String, headers: Headers): Boolean {
        val parsed = runCatching { loginResponseAdapter.fromJson(body) }.getOrNull()
        val fromBody = !parsed?.accessTokenValue().isNullOrBlank() || !parsed?.refreshTokenValue().isNullOrBlank()
        val fromCookie = !buildCookieHeader(headers).isNullOrBlank()
        return fromBody || fromCookie
    }

    private suspend fun applySessionFromLoginBody(
        bodyString: String,
        headers: Headers,
        savedIdentifier: String,
    ): Result<Unit> {
        val parsed = runCatching { loginResponseAdapter.fromJson(bodyString) }.getOrNull()
        val access = parsed?.accessTokenValue()
        val refresh = parsed?.refreshTokenValue()
        val cookies = buildCookieHeader(headers)
        if (access.isNullOrBlank() && cookies.isNullOrBlank()) {
            return Result.failure(IllegalStateException("no_session_in_login_response"))
        }
        authTokenDataSource.saveSession(
            accessToken = access,
            refreshToken = refresh,
            sessionCookieHeader = cookies,
            identifier = savedIdentifier,
        )
        authSessionMemory.setSession(bearer = access, cookie = cookies)
        return Result.success(Unit)
    }

    override suspend fun restoreSessionInMemory() = withContext(Dispatchers.IO) {
        val access = authTokenDataSource.getAccessToken()
        val cookie = authTokenDataSource.getSessionCookieHeader()
        authSessionMemory.setSession(bearer = access, cookie = cookie)
    }

    override suspend fun hasPersistedCredentials(): Boolean = withContext(Dispatchers.IO) {
        !authTokenDataSource.getAccessToken().isNullOrBlank() ||
            !authTokenDataSource.getRefreshToken().isNullOrBlank() ||
            !authTokenDataSource.getSessionCookieHeader().isNullOrBlank()
    }

    override suspend fun fetchSelfProfile(): Result<UserProfile> = withContext(Dispatchers.IO) {
        if (authSessionMemory.bearerToken.isNullOrBlank() &&
            authSessionMemory.cookieHeader.isNullOrBlank()
        ) {
            return@withContext Result.failure(IllegalStateException("no_session"))
        }
        try {
            val r = authApiService.getSelf()
            if (r.isSuccessful) {
                val body = r.body() ?: return@withContext Result.failure(IllegalStateException("empty_self"))
                val name = body.displayOrUsername().orEmpty()
                return@withContext Result.success(
                    UserProfile(
                        id = body.id,
                        displayName = if (name.isNotBlank()) name else "User",
                        username = body.username,
                    ),
                )
            }
            if (r.code() == 401) {
                clearAllSession()
            }
            Result.failure(HttpException(r))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() = withContext(Dispatchers.IO) {
        clearAllSession()
    }

    private suspend fun clearAllSession() {
        authTokenDataSource.clear()
        authSessionMemory.clear()
    }

    private fun buildCookieHeader(headers: Headers): String? {
        val parts = headers.values("Set-Cookie")
        if (parts.isEmpty()) return null
        return parts
            .map { it.substringBefore(';').trim() }
            .filter { it.isNotEmpty() }
            .joinToString("; ")
    }

    private fun httpFailure(
        errorBody: String?,
        successBody: String,
        code: Int,
    ): Result<Unit> {
        val raw = errorBody?.takeIf { it.isNotBlank() } ?: successBody.takeIf { it.isNotBlank() }.orEmpty()
        val fromApi = ApiErrorMessageParser.parseUserMessage(raw)
        val text = fromApi?.takeIf { it.isNotBlank() }
            ?: raw.takeIf { it.isNotBlank() }
            ?: "HTTP $code"
        return Result.failure(ServerErrorException(message = text, statusCode = code))
    }

    private companion object {
        private val JSON_MEDIA = "application/json; charset=utf-8".toMediaType()
    }
}
