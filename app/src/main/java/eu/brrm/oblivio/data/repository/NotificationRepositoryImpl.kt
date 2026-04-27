package eu.brrm.oblivio.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.brrm.oblivio.data.local.OnboardingLocalDataSource
import eu.brrm.oblivio.data.remote.NotificationApiService
import eu.brrm.oblivio.data.remote.dto.NotificationTokenRequestDto
import eu.brrm.oblivio.domain.model.PostLoginDestination
import eu.brrm.oblivio.domain.repository.NotificationRepository
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class NotificationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val onboardingLocalDataSource: OnboardingLocalDataSource,
    private val notificationApiService: NotificationApiService,
    private val firebaseMessaging: FirebaseMessaging,
) : NotificationRepository {
    override suspend fun markPromptHandled(enabled: Boolean) {
        onboardingLocalDataSource.setNotificationPromptHandled(isEnabled = enabled)
    }

    override suspend fun isPromptHandled(): Boolean {
        return onboardingLocalDataSource.isNotificationPromptHandled()
    }

    override suspend fun markPermissionRequestAttempted() {
        onboardingLocalDataSource.setNotificationPermissionRequestAttempted()
    }

    override suspend fun wasPermissionRequestAttempted(): Boolean {
        return onboardingLocalDataSource.hasNotificationPermissionRequestAttempted()
    }

    override suspend fun subscribeCurrentDevice(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = firebaseMessaging.awaitToken()
            val deviceId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID,
            ).orEmpty()
            if (token.isBlank() || deviceId.isBlank()) {
                return@withContext Result.failure(
                    IllegalStateException("missing_notification_device_token"),
                )
            }
            val response = notificationApiService.subscribeDevice(
                NotificationTokenRequestDto(
                    token = token,
                    platform = PLATFORM_ANDROID,
                    deviceId = deviceId,
                ),
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resolvePostLoginDestination(): PostLoginDestination {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            onboardingLocalDataSource.setNotificationPromptHandled(isEnabled = true)
            return PostLoginDestination.HOME
        }

        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) {
            onboardingLocalDataSource.setNotificationPromptHandled(isEnabled = true)
            return PostLoginDestination.HOME
        }

        return PostLoginDestination.NOTIFICATION_PERMISSION
    }

    private suspend fun FirebaseMessaging.awaitToken(): String =
        suspendCancellableCoroutine { continuation ->
            token
                .addOnSuccessListener { value ->
                    if (continuation.isActive) continuation.resume(value)
                }
                .addOnFailureListener { exception ->
                    if (continuation.isActive) continuation.resumeWithException(exception)
                }
                .addOnCanceledListener {
                    continuation.cancel()
                }
        }

    private companion object {
        const val PLATFORM_ANDROID = "android"
    }
}
