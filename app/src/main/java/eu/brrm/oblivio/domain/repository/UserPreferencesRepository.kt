package eu.brrm.oblivio.domain.repository

import eu.brrm.oblivio.domain.model.AppThemeMode
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val appThemeMode: Flow<AppThemeMode>
    val pushChatEnabled: Flow<Boolean>
    val pushEmailEnabled: Flow<Boolean>
    val pushCallEnabled: Flow<Boolean>
    suspend fun setAppThemeMode(mode: AppThemeMode)
    suspend fun setPushChatEnabled(enabled: Boolean)
    suspend fun setPushEmailEnabled(enabled: Boolean)
    suspend fun setPushCallEnabled(enabled: Boolean)
}
