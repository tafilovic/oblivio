package eu.brrm.oblivio.ui.splash

import eu.brrm.oblivio.ui.navigation.OblivioDestination

data class SplashState(
    val isLoading: Boolean = true,
)

sealed interface SplashIntent {
    data object OnAppear : SplashIntent
}

sealed interface SplashEffect {
    data class Navigate(val destination: OblivioDestination) : SplashEffect
}
