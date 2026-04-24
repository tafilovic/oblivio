package eu.brrm.oblivio.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.brrm.oblivio.domain.model.PostLoginDestination
import eu.brrm.oblivio.domain.repository.SplashRepository
import eu.brrm.oblivio.domain.repository.SplashStartDestination
import eu.brrm.oblivio.ui.navigation.OblivioDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val splashRepository: SplashRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(SplashState())
    val state: StateFlow<SplashState> = _state.asStateFlow()

    private val _effect = Channel<SplashEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: SplashIntent) {
        when (intent) {
            SplashIntent.OnAppear -> load()
        }
    }

    private fun load() {
        viewModelScope.launch {
            when (val next = splashRepository.resolveStartDestination()) {
                is SplashStartDestination.SignIn -> {
                    _effect.send(SplashEffect.Navigate(OblivioDestination.SignIn))
                }
                is SplashStartDestination.AfterLogin -> {
                    val dest = when (next.destination) {
                        PostLoginDestination.HOME -> OblivioDestination.Home
                        PostLoginDestination.NOTIFICATION_PERMISSION -> OblivioDestination.NotificationPermission
                    }
                    _effect.send(SplashEffect.Navigate(dest))
                }
            }
        }
    }
}
