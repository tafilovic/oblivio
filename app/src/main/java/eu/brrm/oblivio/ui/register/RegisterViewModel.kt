package eu.brrm.oblivio.ui.register

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
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    private val _effect = Channel<RegisterEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: RegisterIntent) {
        when (intent) {
            is RegisterIntent.UsernameChanged -> _state.value = _state.value.copy(
                username = intent.value,
                errorMessageResId = null,
                errorMessageText = null,
            )

            is RegisterIntent.EmailChanged -> _state.value = _state.value.copy(
                email = intent.value,
                errorMessageResId = null,
                errorMessageText = null,
            )

            is RegisterIntent.PasswordChanged -> _state.value = _state.value.copy(
                password = intent.value,
                errorMessageResId = null,
                errorMessageText = null,
            )

            is RegisterIntent.ConfirmPasswordChanged -> _state.value = _state.value.copy(
                confirmPassword = intent.value,
                errorMessageResId = null,
                errorMessageText = null,
            )

            RegisterIntent.TogglePasswordVisibility -> _state.value = _state.value.copy(
                isPasswordVisible = !_state.value.isPasswordVisible,
            )

            RegisterIntent.ToggleConfirmPasswordVisibility -> _state.value = _state.value.copy(
                isConfirmPasswordVisible = !_state.value.isConfirmPasswordVisible,
            )

            RegisterIntent.Submit -> submit()
        }
    }

    private fun submit() {
        if (_state.value.password != _state.value.confirmPassword) {
            _state.value = _state.value.copy(
                errorMessageResId = R.string.error_password_mismatch,
                errorMessageText = null,
            )
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isSubmitting = true,
                errorMessageResId = null,
                errorMessageText = null,
            )
            val result = authRepository.register(
                username = _state.value.username.trim(),
                password = _state.value.password,
                email = _state.value.email.trim().takeIf { it.isNotEmpty() },
            )
            if (result.isSuccess) {
                _state.value = _state.value.copy(isSubmitting = false)
                notificationRepository.subscribeCurrentDevice()
                val postLogin = notificationRepository.resolvePostLoginDestination()
                _effect.send(RegisterEffect.NavigateAfterRegister(destination = postLogin))
            } else {
                val (text, resId) = when (val e = result.exceptionOrNull()) {
                    is ServerErrorException -> e.message to null
                    is IOException -> null to R.string.error_network
                    else -> null to R.string.error_register_failed
                }
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    errorMessageText = text,
                    errorMessageResId = if (text != null) null else resId,
                )
            }
        }
    }

}
