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
) {
    val savedIdentifier: kotlinx.coroutines.flow.Flow<String> = dataStore.data
        .map { it[Keys.identifier] ?: "" }

    suspend fun getAccessToken(): String? = dataStore.data
        .map { it[Keys.accessToken] }
        .first()

    suspend fun getRefreshToken(): String? = dataStore.data
        .map { it[Keys.refreshToken] }
        .first()

    suspend fun getSessionCookieHeader(): String? = dataStore.data
        .map { it[Keys.sessionCookie] }
        .first()

    suspend fun getLastIdentifier(): String? = dataStore.data
        .map { it[Keys.identifier] }
        .first()
        .takeIf { !it.isNullOrBlank() }

    suspend fun saveSession(
        accessToken: String?,
        refreshToken: String?,
        sessionCookieHeader: String?,
        identifier: String?,
    ) {
        dataStore.edit { prefs ->
            if (accessToken != null) prefs[Keys.accessToken] = accessToken
            else prefs.remove(Keys.accessToken)
            if (refreshToken != null) prefs[Keys.refreshToken] = refreshToken
            else prefs.remove(Keys.refreshToken)
            if (sessionCookieHeader != null) prefs[Keys.sessionCookie] = sessionCookieHeader
            else prefs.remove(Keys.sessionCookie)
            if (identifier != null) prefs[Keys.identifier] = identifier
        }
    }

    suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.remove(Keys.accessToken)
            prefs.remove(Keys.refreshToken)
            prefs.remove(Keys.sessionCookie)
            prefs.remove(Keys.identifier)
        }
    }

    private object Keys {
        val accessToken = stringPreferencesKey("auth_access_token")
        val refreshToken = stringPreferencesKey("auth_refresh_token")
        val sessionCookie = stringPreferencesKey("auth_session_cookie")
        val identifier = stringPreferencesKey("auth_last_identifier")
    }
}
