package eu.brrm.oblivio.data.repository

import eu.brrm.oblivio.domain.model.ProfileSummary
import eu.brrm.oblivio.domain.repository.AuthRepository
import eu.brrm.oblivio.domain.repository.ProfileRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val authRepository: AuthRepository,
) : ProfileRepository {
    override suspend fun loadProfile(): ProfileSummary = withContext(Dispatchers.IO) {
        authRepository.fetchSelfProfile()
            .map { ProfileSummary(displayName = it.displayName) }
            .getOrElse {
                ProfileSummary(
                    displayName = "User",
                )
            }
    }
}
