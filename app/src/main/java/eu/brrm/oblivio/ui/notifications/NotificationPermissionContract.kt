package eu.brrm.oblivio.ui.notifications

data class NotificationPermissionState(
    val isSaving: Boolean = false,
    val hasRequestedSystemPermission: Boolean = false,
    val showSettingsDialog: Boolean = false,
    val hasOpenedSettings: Boolean = false,
)

sealed interface NotificationPermissionIntent {
    data object EnableClicked : NotificationPermissionIntent
    data object SystemPermissionRequestStarted : NotificationPermissionIntent
    data object SystemPermissionUnavailable : NotificationPermissionIntent
    data class PermissionResult(val isGranted: Boolean) : NotificationPermissionIntent
    data object SettingsDialogDismissed : NotificationPermissionIntent
    data object OpenSettingsClicked : NotificationPermissionIntent
}

sealed interface NotificationPermissionEffect {
    data object RequestSystemPermission : NotificationPermissionEffect
    data object OpenNotificationSettings : NotificationPermissionEffect
    data object NavigateNext : NotificationPermissionEffect
    data object CloseApp : NotificationPermissionEffect
}
