package eu.brrm.oblivio.ui.notifications

data class NotificationPermissionState(
    val isSaving: Boolean = false,
)

sealed interface NotificationPermissionIntent {
    data object EnableClicked : NotificationPermissionIntent
    data class PermissionResult(val isGranted: Boolean) : NotificationPermissionIntent
    data object MaybeLaterClicked : NotificationPermissionIntent
}

sealed interface NotificationPermissionEffect {
    data object RequestSystemPermission : NotificationPermissionEffect
    data object NavigateNext : NotificationPermissionEffect
}
