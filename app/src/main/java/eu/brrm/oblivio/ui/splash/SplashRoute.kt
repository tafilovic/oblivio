package eu.brrm.oblivio.ui.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import eu.brrm.oblivio.ui.navigation.OblivioDestination
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SplashRoute(
    onNavigate: (OblivioDestination) -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.onIntent(SplashIntent.OnAppear)
    }
    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is SplashEffect.Navigate -> onNavigate(effect.destination)
            }
        }
    }
    SplashScreen()
}
