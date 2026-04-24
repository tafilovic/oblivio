package eu.brrm.oblivio.ui.profile

import androidx.annotation.StringRes
import eu.brrm.oblivio.domain.model.AppThemeMode

data class ProfileState(
    val displayName: String = "",
    val pushChat: Boolean = true,
    val pushEmail: Boolean = true,
    val pushCall: Boolean = true,
    val appTheme: AppThemeMode = AppThemeMode.System,
    val isLoading: Boolean = true,
    @StringRes val errorMessageResId: Int? = null,
    val showLogoutDialog: Boolean = false,
)

sealed interface ProfileIntent {
    data object OnAppear : ProfileIntent
    data class PushChatChanged(val value: Boolean) : ProfileIntent
    data class PushEmailChanged(val value: Boolean) : ProfileIntent
    data class PushCallChanged(val value: Boolean) : ProfileIntent
    data class ThemeSelected(val mode: AppThemeMode) : ProfileIntent
    data object LogoutClicked : ProfileIntent
    data object LogoutDialogDismissed : ProfileIntent
    data object LogoutDialogConfirmed : ProfileIntent
}

sealed interface ProfileEffect {
    data object NavigateToSignIn : ProfileEffect
}
