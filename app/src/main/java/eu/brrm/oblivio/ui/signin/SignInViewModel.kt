package eu.brrm.oblivio.ui.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.brrm.oblivio.R
import eu.brrm.oblivio.domain.ServerErrorException
import eu.brrm.oblivio.domain.repository.AuthRepository
import java.io.IOException
import eu.brrm.oblivio.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state: StateFlow<SignInState> = _state.asStateFlow()

    private val _effect = Channel<SignInEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: SignInIntent) {
        when (intent) {
            is SignInIntent.UsernameChanged -> _state.value = _state.value.copy(
                usernameOrEmail = intent.value,
                errorMessageResId = null,
                errorMessageText = null,
            )

            is SignInIntent.PasswordChanged -> _state.value = _state.value.copy(
                password = intent.value,
                errorMessageResId = null,
                errorMessageText = null,
            )

            SignInIntent.TogglePasswordVisibility -> _state.value = _state.value.copy(
                isPasswordVisible = !_state.value.isPasswordVisible,
            )

            SignInIntent.Submit -> submit()
            SignInIntent.OpenRegister -> navigateToRegister()
        }
    }

    private fun submit() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isSubmitting = true,
                errorMessageResId = null,
                errorMessageText = null,
            )
            val result = authRepository.signIn(
                usernameOrEmail = _state.value.usernameOrEmail.trim(),
                password = _state.value.password,
            )
            if (result.isSuccess) {
                _state.value = _state.value.copy(isSubmitting = false)
                val postLogin = notificationRepository.resolvePostLoginDestination()
                _effect.send(SignInEffect.NavigateAfterSignIn(destination = postLogin))
            } else {
                val (text, resId) = when (val e = result.exceptionOrNull()) {
                    is ServerErrorException -> e.message to null
                    is IOException -> null to R.string.error_network
                    else -> null to R.string.error_signin_failed
                }
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    errorMessageText = text,
                    errorMessageResId = if (text != null) null else resId,
                )
            }
        }
    }

    private fun navigateToRegister() {
        viewModelScope.launch {
            _effect.send(SignInEffect.NavigateToRegister)
        }
    }

}
