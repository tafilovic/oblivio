package eu.brrm.oblivio.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import eu.brrm.oblivio.data.auth.FakeAuthSessionStore
import eu.brrm.oblivio.data.auth.RecordingChain
import eu.brrm.oblivio.data.auth.testRequest
import eu.brrm.oblivio.data.auth.testResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import okhttp3.Headers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RefreshTokenInterceptorTest {
    @Test
    fun refreshesTokensAndRetriesOriginalRequestOn401() {
        val store = FakeAuthSessionStore().apply {
            accessToken = "old-access"
            refreshToken = "old-refresh"
            sessionCookieHeader = "csrf_refresh=old-refresh; other=value"
        }
        val memory = AuthSessionMemory().apply {
            setSession("old-access", "csrf_refresh=old-refresh; other=value")
        }
        val interceptor = RefreshTokenInterceptor(
            authSessionStore = store,
            sessionMemory = memory,
            logoutNotifier = AuthLogoutNotifier(),
            moshi = testMoshi(),
        )
        val original = testRequest()
        lateinit var refreshRequestHeader: String
        lateinit var retryAuthHeader: String
        lateinit var retryCookieHeader: String
        val chain = RecordingChain(
            currentRequest = original,
            responses = listOf(
                { request -> testResponse(request, 401) },
                { request ->
                    refreshRequestHeader = request.header("x-csrf").orEmpty()
                    testResponse(
                        request = request,
                        code = 200,
                        body = """{"accessToken":"new-access"}""",
                        headers = Headers.headersOf(
                            "Set-Cookie",
                            "csrf_refresh=new-refresh; Path=/; HttpOnly",
                        ),
                    )
                },
                { request ->
                    retryAuthHeader = request.header("Authorization").orEmpty()
                    retryCookieHeader = request.header("Cookie").orEmpty()
                    testResponse(request, 200, """{"ok":true}""")
                },
            ),
        )

        val response = interceptor.intercept(chain)

        assertEquals(200, response.code)
        assertEquals("old-refresh", refreshRequestHeader)
        assertEquals("Bearer new-access", retryAuthHeader)
        assertEquals("csrf_refresh=new-refresh; other=value", retryCookieHeader)
        assertEquals("new-access", store.accessToken)
        assertEquals("new-refresh", store.refreshToken)
        assertEquals("csrf_refresh=new-refresh; other=value", store.sessionCookieHeader)
        assertEquals("new-access", memory.bearerToken)
        assertEquals("csrf_refresh=new-refresh; other=value", memory.cookieHeader)
        assertEquals(3, chain.requests.size)
    }

    @Test
    fun api401CallsRefreshAndSavesNewAccessTokenAndRefreshToken() {
        val store = FakeAuthSessionStore().apply {
            accessToken = "expired-access"
            refreshToken = "current-refresh"
            sessionCookieHeader = "csrf_refresh=current-refresh"
        }
        val memory = AuthSessionMemory().apply {
            setSession("expired-access", "csrf_refresh=current-refresh")
        }
        val interceptor = RefreshTokenInterceptor(
            authSessionStore = store,
            sessionMemory = memory,
            logoutNotifier = AuthLogoutNotifier(),
            moshi = testMoshi(),
        )
        val original = testRequest("/users/me/")
        val chain = RecordingChain(
            currentRequest = original,
            responses = listOf(
                { request -> testResponse(request, 401) },
                { request ->
                    assertEquals("/auth/refresh", request.url.encodedPath)
                    assertEquals("POST", request.method)
                    assertEquals("Bearer expired-access", request.header("Authorization"))
                    assertEquals("csrf_refresh=current-refresh", request.header("Cookie"))
                    assertEquals("current-refresh", request.header("x-csrf"))
                    testResponse(
                        request = request,
                        code = 200,
                        body = """{"accessToken":"fresh-access"}""",
                        headers = Headers.headersOf(
                            "Set-Cookie",
                            "csrf_refresh=fresh-refresh; Path=/; HttpOnly",
                        ),
                    )
                },
                { request ->
                    assertEquals("/users/me/", request.url.encodedPath)
                    assertEquals("Bearer fresh-access", request.header("Authorization"))
                    assertEquals("csrf_refresh=fresh-refresh", request.header("Cookie"))
                    testResponse(request, 200, """{"id":"user-1"}""")
                },
            ),
        )

        val response = interceptor.intercept(chain)

        assertEquals(200, response.code)
        assertEquals("fresh-access", store.accessToken)
        assertEquals("fresh-refresh", store.refreshToken)
        assertEquals("csrf_refresh=fresh-refresh", store.sessionCookieHeader)
        assertEquals("fresh-access", memory.bearerToken)
        assertEquals("csrf_refresh=fresh-refresh", memory.cookieHeader)
    }

    @Test
    fun clearsSessionAndEmitsLogoutWhenRefreshFailsThreeTimes() = runBlocking {
        val store = FakeAuthSessionStore().apply {
            accessToken = "old-access"
            refreshToken = "old-refresh"
            sessionCookieHeader = "csrf_refresh=old-refresh"
        }
        val memory = AuthSessionMemory().apply {
            setSession("old-access", "csrf_refresh=old-refresh")
        }
        val logoutNotifier = AuthLogoutNotifier()
        val logoutEvent = async { withTimeout(1_000) { logoutNotifier.events.first() } }
        val interceptor = RefreshTokenInterceptor(
            authSessionStore = store,
            sessionMemory = memory,
            logoutNotifier = logoutNotifier,
            moshi = testMoshi(),
        )
        val original = testRequest()
        val chain = RecordingChain(
            currentRequest = original,
            responses = listOf(
                { request -> testResponse(request, 401) },
                { request -> testResponse(request, 500) },
                { request -> testResponse(request, 500) },
                { request -> testResponse(request, 500) },
            ),
        )

        val response = interceptor.intercept(chain)
        logoutEvent.await()

        assertEquals(401, response.code)
        assertNull(store.accessToken)
        assertNull(store.refreshToken)
        assertNull(store.sessionCookieHeader)
        assertNull(memory.bearerToken)
        assertNull(memory.cookieHeader)
        assertEquals(1, store.clearCount)
        assertEquals(4, chain.requests.size)
    }

    private fun testMoshi(): Moshi =
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
}
