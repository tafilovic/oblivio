package eu.brrm.oblivio.ui.navigation

sealed class OblivioDestination(val route: String) {
    data object Splash : OblivioDestination("splash")
    data object SignIn : OblivioDestination("sign_in")
    data object Register : OblivioDestination("register")
    data object NotificationPermission : OblivioDestination("notification_permission")
    data object Home : OblivioDestination("home")
}
