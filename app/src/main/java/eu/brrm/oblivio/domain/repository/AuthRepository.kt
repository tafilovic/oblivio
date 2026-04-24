package eu.brrm.oblivio.domain.repository

import eu.brrm.oblivio.domain.model.UserProfile

interface AuthRepository {
    suspend fun signIn(usernameOrEmail: String, password: String): Result<Unit>
    /** Optional [email] is only sent to the server when non-blank. */
    suspend fun register(username: String, password: String, email: String? = null): Result<Unit>
    /** Loads persisted tokens into the OkHttp session (call on cold start before authorized API calls). */
    suspend fun restoreSessionInMemory()
    /** True if DataStore has any access token, refresh token, or session cookie. */
    suspend fun hasPersistedCredentials(): Boolean
    /**
     * Current user; requires a valid access token or session cookie. Clears local session on 401.
     */
    suspend fun fetchSelfProfile(): Result<UserProfile>
    /** Clears the local session. Implement with token/refresh clearing when available. */
    suspend fun signOut()
}
