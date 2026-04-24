package eu.brrm.oblivio.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Singleton
class UserPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val themeKeyFlow = dataStore.data.map { prefs ->
        prefs[Keys.appTheme] ?: "system"
    }

    val pushChatFlow = dataStore.data.map { it[Keys.pushChat] ?: true }
    val pushEmailFlow = dataStore.data.map { it[Keys.pushEmail] ?: true }
    val pushCallFlow = dataStore.data.map { it[Keys.pushCall] ?: true }

    suspend fun setAppTheme(value: String) {
        dataStore.edit { it[Keys.appTheme] = value }
    }

    suspend fun getAppThemeString(): String {
        return themeKeyFlow.first()
    }

    suspend fun setPushChatEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.pushChat] = enabled }
    }

    suspend fun setPushEmailEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.pushEmail] = enabled }
    }

    suspend fun setPushCallEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.pushCall] = enabled }
    }

    private object Keys {
        val appTheme = stringPreferencesKey("app_theme")
        val pushChat = booleanPreferencesKey("push_chat")
        val pushEmail = booleanPreferencesKey("push_email")
        val pushCall = booleanPreferencesKey("push_call")
    }
}
