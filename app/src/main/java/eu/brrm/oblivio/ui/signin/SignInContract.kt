package eu.brrm.oblivio.ui.signin

import androidx.annotation.StringRes
import eu.brrm.oblivio.domain.model.PostLoginDestination

data class SignInState(
    val usernameOrEmail: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isSubmitting: Boolean = false,
    /**
     * Server-provided or parsed API message. When null, [errorMessageResId] is used as a localized fallback
     * (or both may be set only during transition; UI prefers [errorMessageText] when non-null).
     * Future: map known codes in the data layer to [errorMessageResId] only.
     */
    val errorMessageText: String? = null,
    @StringRes val errorMessageResId: Int? = null,
)

sealed interface SignInIntent {
    data class UsernameChanged(val value: String) : SignInIntent
    data class PasswordChanged(val value: String) : SignInIntent
    data object TogglePasswordVisibility : SignInIntent
    data object Submit : SignInIntent
    data object OpenRegister : SignInIntent
}

sealed interface SignInEffect {
    data object NavigateToRegister : SignInEffect
    data class NavigateAfterSignIn(val destination: PostLoginDestination) : SignInEffect
}
