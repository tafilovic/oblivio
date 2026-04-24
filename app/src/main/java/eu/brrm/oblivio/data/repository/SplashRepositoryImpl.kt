package eu.brrm.oblivio.data.repository

import eu.brrm.oblivio.domain.repository.AuthRepository
import eu.brrm.oblivio.domain.repository.NotificationRepository
import eu.brrm.oblivio.domain.repository.SplashRepository
import eu.brrm.oblivio.domain.repository.SplashStartDestination
import javax.inject.Inject
import kotlinx.coroutines.delay

class SplashRepositoryImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository,
) : SplashRepository {
    override suspend fun resolveStartDestination(): SplashStartDestination {
        delay(1100)
        authRepository.restoreSessionInMemory()
        if (!authRepository.hasPersistedCredentials()) {
            return SplashStartDestination.SignIn
        }
        if (authRepository.fetchSelfProfile().isSuccess) {
            val afterLogin = notificationRepository.resolvePostLoginDestination()
            return SplashStartDestination.AfterLogin(afterLogin)
        }
        if (!authRepository.hasPersistedCredentials()) {
            return SplashStartDestination.SignIn
        }
        val afterLogin = notificationRepository.resolvePostLoginDestination()
        return SplashStartDestination.AfterLogin(afterLogin)
    }
}
