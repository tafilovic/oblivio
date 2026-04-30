package eu.brrm.oblivio.data.local

interface AuthSessionStore {
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun getSessionCookieHeader(): String?
    suspend fun saveSession(
        accessToken: String?,
        refreshToken: String?,
        sessionCookieHeader: String?,
    )
    suspend fun saveAccessAndCookie(
        accessToken: String,
        sessionCookieHeader: String,
    )
    suspend fun clear()
}
