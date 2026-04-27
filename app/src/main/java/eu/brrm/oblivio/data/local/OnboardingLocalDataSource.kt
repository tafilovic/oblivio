package eu.brrm.oblivio.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OnboardingLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    suspend fun isNotificationPromptHandled(): Boolean {
        return dataStore.data
            .map { it[notificationPromptHandledKey] ?: false }
            .first()
    }

    suspend fun setNotificationPromptHandled(isEnabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[notificationPromptHandledKey] = true
            prefs[notificationPermissionEnabledKey] = isEnabled
        }
    }

    suspend fun hasNotificationPermissionRequestAttempted(): Boolean {
        return dataStore.data
            .map { it[notificationPermissionRequestAttemptedKey] ?: false }
            .first()
    }

    suspend fun setNotificationPermissionRequestAttempted() {
        dataStore.edit { prefs ->
            prefs[notificationPermissionRequestAttemptedKey] = true
        }
    }

    private companion object {
        val notificationPromptHandledKey = booleanPreferencesKey("notification_prompt_handled")
        val notificationPermissionEnabledKey = booleanPreferencesKey("notification_permission_enabled")
        val notificationPermissionRequestAttemptedKey =
            booleanPreferencesKey("notification_permission_request_attempted")
    }
}
