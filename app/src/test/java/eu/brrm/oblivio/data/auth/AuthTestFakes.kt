package eu.brrm.oblivio.data.auth

import eu.brrm.oblivio.data.local.AuthSessionStore
import eu.brrm.oblivio.data.remote.AuthApiService
import eu.brrm.oblivio.data.remote.dto.LoginRequestDto
import eu.brrm.oblivio.data.remote.dto.SelfUserDto
import okhttp3.Call
import okhttp3.Connection
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class FakeAuthSessionStore : AuthSessionStore {
    var accessToken: String? = null
    var refreshToken: String? = null
    var sessionCookieHeader: String? = null
    var clearCount = 0

    override suspend fun getAccessToken(): String? = accessToken

    override suspend fun getRefreshToken(): String? = refreshToken

    override suspend fun getSessionCookieHeader(): String? = sessionCookieHeader

    override suspend fun saveSession(
        accessToken: String?,
        refreshToken: String?,
        sessionCookieHeader: String?,
    ) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        this.sessionCookieHeader = sessionCookieHeader
    }

    override suspend fun saveAccessAndCookie(
        accessToken: String,
        sessionCookieHeader: String,
    ) {
        this.accessToken = accessToken
        this.sessionCookieHeader = sessionCookieHeader
        this.refreshToken = extractCookieValue(sessionCookieHeader, "csrf_refresh")
    }

    override suspend fun clear() {
        accessToken = null
        refreshToken = null
        sessionCookieHeader = null
        clearCount += 1
    }

    private fun extractCookieValue(cookieHeader: String, name: String): String? =
        cookieHeader
            .split(';')
            .map { it.trim() }
            .firstOrNull { it.startsWith("$name=") }
            ?.substringAfter('=')
            ?.takeIf { it.isNotBlank() }
}

class FakeAuthApiService(
    private val loginResponse: Response<ResponseBody>,
) : AuthApiService {
    var loginRequest: LoginRequestDto? = null

    override suspend fun login(body: LoginRequestDto): Response<ResponseBody> {
        loginRequest = body
        return loginResponse
    }

    override suspend fun registerUser(body: RequestBody): Response<ResponseBody> =
        error("registerUser not used")

    override suspend fun getSelf(): Response<SelfUserDto> =
        error("getSelf not used")
}

class RecordingChain(
    private var currentRequest: Request,
    responses: List<(Request) -> okhttp3.Response>,
) : Interceptor.Chain {
    private val pending = ArrayDeque<(Request) -> okhttp3.Response>().apply {
        addAll(responses)
    }
    private val client = OkHttpClient()
    val requests = mutableListOf<Request>()

    override fun request(): Request = currentRequest

    override fun proceed(request: Request): okhttp3.Response {
        requests += request
        currentRequest = request
        return pending.removeFirst().invoke(request)
    }

    override fun connection(): Connection? = null

    override fun call(): Call = client.newCall(currentRequest)

    override fun connectTimeoutMillis(): Int = 1_000

    override fun withConnectTimeout(timeout: Int, unit: java.util.concurrent.TimeUnit): Interceptor.Chain = this

    override fun readTimeoutMillis(): Int = 1_000

    override fun withReadTimeout(timeout: Int, unit: java.util.concurrent.TimeUnit): Interceptor.Chain = this

    override fun writeTimeoutMillis(): Int = 1_000

    override fun withWriteTimeout(timeout: Int, unit: java.util.concurrent.TimeUnit): Interceptor.Chain = this
}

fun testRequest(path: String = "/protected"): Request =
    Request.Builder()
        .url("https://devapi.oblivio.brrm.eu$path")
        .get()
        .build()

fun testResponse(
    request: Request,
    code: Int,
    body: String = "",
    headers: Headers = Headers.headersOf(),
): okhttp3.Response =
    okhttp3.Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_1_1)
        .code(code)
        .message("HTTP $code")
        .headers(headers)
        .body(body.toResponseBody("application/json".toMediaType()))
        .build()
