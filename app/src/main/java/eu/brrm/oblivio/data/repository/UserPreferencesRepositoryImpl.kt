package eu.brrm.oblivio.data.repository

import eu.brrm.oblivio.data.local.UserPreferencesDataSource
import eu.brrm.oblivio.domain.model.AppThemeMode
import eu.brrm.oblivio.domain.repository.UserPreferencesRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataSource: UserPreferencesDataSource,
) : UserPreferencesRepository {
    override val appThemeMode: Flow<AppThemeMode> = dataSource.themeKeyFlow
        .map { key ->
            when (key) {
                "light" -> AppThemeMode.Light
                "dark" -> AppThemeMode.Dark
                else -> AppThemeMode.System
            }
        }
        .distinctUntilChanged()

    override val pushChatEnabled: Flow<Boolean> = dataSource.pushChatFlow
    override val pushEmailEnabled: Flow<Boolean> = dataSource.pushEmailFlow
    override val pushCallEnabled: Flow<Boolean> = dataSource.pushCallFlow

    override suspend fun setAppThemeMode(mode: AppThemeMode) {
        val s = when (mode) {
            AppThemeMode.Light -> "light"
            AppThemeMode.Dark -> "dark"
            AppThemeMode.System -> "system"
        }
        dataSource.setAppTheme(s)
    }

    override suspend fun setPushChatEnabled(enabled: Boolean) = dataSource.setPushChatEnabled(enabled)
    override suspend fun setPushEmailEnabled(enabled: Boolean) = dataSource.setPushEmailEnabled(enabled)
    override suspend fun setPushCallEnabled(enabled: Boolean) = dataSource.setPushCallEnabled(enabled)
}
