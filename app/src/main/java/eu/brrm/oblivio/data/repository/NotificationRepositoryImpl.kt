package eu.brrm.oblivio.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.brrm.oblivio.data.local.OnboardingLocalDataSource
import eu.brrm.oblivio.domain.model.PostLoginDestination
import eu.brrm.oblivio.domain.repository.NotificationRepository
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val onboardingLocalDataSource: OnboardingLocalDataSource,
) : NotificationRepository {
    override suspend fun markPromptHandled(enabled: Boolean) {
        onboardingLocalDataSource.setNotificationPromptHandled(isEnabled = enabled)
    }

    override suspend fun isPromptHandled(): Boolean {
        return onboardingLocalDataSource.isNotificationPromptHandled()
    }

    override suspend fun resolvePostLoginDestination(): PostLoginDestination {
        if (onboardingLocalDataSource.isNotificationPromptHandled()) {
            return PostLoginDestination.HOME
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
            if (granted) {
                onboardingLocalDataSource.setNotificationPromptHandled(isEnabled = true)
                return PostLoginDestination.HOME
            }
        }
        return PostLoginDestination.NOTIFICATION_PERMISSION
    }
}
