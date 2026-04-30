package eu.brrm.oblivio.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Singleton
class AuthTokenDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : AuthSessionStore {
    override suspend fun getAccessToken(): String? = dataStore.data
        .map { it[Keys.accessToken] }
        .first()

    override suspend fun getRefreshToken(): String? = dataStore.data
        .map { it[Keys.refreshToken] }
        .first()

    override suspend fun getSessionCookieHeader(): String? = dataStore.data
        .map { it[Keys.sessionCookie] }
        .first()

    override suspend fun saveSession(
        accessToken: String?,
        refreshToken: String?,
        sessionCookieHeader: String?,
    ) {
        dataStore.edit { prefs ->
            if (accessToken != null) prefs[Keys.accessToken] = accessToken
            else prefs.remove(Keys.accessToken)
            if (refreshToken != null) prefs[Keys.refreshToken] = refreshToken
            else prefs.remove(Keys.refreshToken)
            if (sessionCookieHeader != null) prefs[Keys.sessionCookie] = sessionCookieHeader
            else prefs.remove(Keys.sessionCookie)
            prefs.remove(Keys.legacyIdentifier)
        }
    }

    override suspend fun saveAccessAndCookie(
        accessToken: String,
        sessionCookieHeader: String,
    ) {
        dataStore.edit { prefs ->
            prefs[Keys.accessToken] = accessToken
            prefs[Keys.sessionCookie] = sessionCookieHeader
            val refreshToken = extractCookieValue(sessionCookieHeader, REFRESH_COOKIE_NAME)
            refreshToken?.let {
                prefs[Keys.refreshToken] = refreshToken
            } ?: prefs.remove(Keys.refreshToken)
            prefs.remove(Keys.legacyIdentifier)
        }
    }

    override suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.remove(Keys.accessToken)
            prefs.remove(Keys.refreshToken)
            prefs.remove(Keys.sessionCookie)
            prefs.remove(Keys.legacyIdentifier)
        }
    }

    private fun extractCookieValue(cookieHeader: String, name: String): String? =
        cookieHeader
            .split(';')
            .map { it.trim() }
            .firstOrNull { it.startsWith("$name=") }
            ?.substringAfter('=')
            ?.takeIf { it.isNotBlank() }

    private object Keys {
        val accessToken = stringPreferencesKey("auth_access_token")
        val refreshToken = stringPreferencesKey("auth_refresh_token")
        val sessionCookie = stringPreferencesKey("auth_session_cookie")
        val legacyIdentifier = stringPreferencesKey("auth_last_identifier")
    }

    private companion object {
        const val REFRESH_COOKIE_NAME = "csrf_refresh"
    }
}
