package eu.brrm.oblivio.ui.register

import androidx.annotation.StringRes
import eu.brrm.oblivio.domain.model.PostLoginDestination

data class RegisterState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isSubmitting: Boolean = false,
    /** API / parsed message; when null, [errorMessageResId] is used. */
    val errorMessageText: String? = null,
    @StringRes val errorMessageResId: Int? = null,
)

sealed interface RegisterIntent {
    data class UsernameChanged(val value: String) : RegisterIntent
    data class EmailChanged(val value: String) : RegisterIntent
    data class PasswordChanged(val value: String) : RegisterIntent
    data class ConfirmPasswordChanged(val value: String) : RegisterIntent
    data object TogglePasswordVisibility : RegisterIntent
    data object ToggleConfirmPasswordVisibility : RegisterIntent
    data object Submit : RegisterIntent
}

sealed interface RegisterEffect {
    data class NavigateAfterRegister(val destination: PostLoginDestination) : RegisterEffect
}
