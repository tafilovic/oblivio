package eu.brrm.oblivio.data.repository

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import eu.brrm.oblivio.data.auth.FakeAuthApiService
import eu.brrm.oblivio.data.auth.FakeAuthSessionStore
import eu.brrm.oblivio.data.remote.AuthSessionMemory
import kotlinx.coroutines.runBlocking
import okhttp3.Headers
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class AuthRepositoryImplTest {
    @Test
    fun signInStoresAccessTokenAndRefreshTokenFromCsrfRefreshCookie() = runBlocking {
        val store = FakeAuthSessionStore()
        val memory = AuthSessionMemory()
        val api = FakeAuthApiService(
            loginResponse = Response.success(
                """{"accessToken":"access-1"}""".toResponseBody(),
                Headers.headersOf(
                    "Set-Cookie",
                    "csrf_refresh=refresh-1; Path=/; HttpOnly",
                    "Set-Cookie",
                    "other=value; Path=/",
                ),
            ),
        )
        val repository = AuthRepositoryImpl(
            authApiService = api,
            authSessionStore = store,
            authSessionMemory = memory,
            moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build(),
        )

        val result = repository.signIn(" user@example.com ", "password")

        assertTrue(result.isSuccess)
        assertEquals("user@example.com", api.loginRequest?.identifier)
        assertEquals("password", api.loginRequest?.password)
        assertEquals("access-1", store.accessToken)
        assertEquals("refresh-1", store.refreshToken)
        assertEquals("csrf_refresh=refresh-1; other=value", store.sessionCookieHeader)
        assertEquals("access-1", memory.bearerToken)
        assertEquals("csrf_refresh=refresh-1; other=value", memory.cookieHeader)
    }

    @Test
    fun loginSuccessPersistsAccessTokenAndRefreshToken() = runBlocking {
        val store = FakeAuthSessionStore()
        val repository = AuthRepositoryImpl(
            authApiService = FakeAuthApiService(
                loginResponse = Response.success(
                    """{"accessToken":"login-access"}""".toResponseBody(),
                    Headers.headersOf(
                        "Set-Cookie",
                        "csrf_refresh=login-refresh; Path=/; HttpOnly",
                    ),
                ),
            ),
            authSessionStore = store,
            authSessionMemory = AuthSessionMemory(),
            moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build(),
        )

        val result = repository.signIn("user", "password")

        assertTrue(result.isSuccess)
        assertEquals("login-access", store.accessToken)
        assertEquals("login-refresh", store.refreshToken)
        assertEquals("csrf_refresh=login-refresh", store.sessionCookieHeader)
    }
}
