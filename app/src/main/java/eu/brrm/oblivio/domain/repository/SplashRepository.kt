package eu.brrm.oblivio.domain.repository

import eu.brrm.oblivio.domain.model.PostLoginDestination

sealed class SplashStartDestination {
    data object SignIn : SplashStartDestination()
    data class AfterLogin(
        val destination: PostLoginDestination,
    ) : SplashStartDestination()
}

interface SplashRepository {
    suspend fun resolveStartDestination(): SplashStartDestination
}
