package eu.brrm.oblivio.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import eu.brrm.oblivio.domain.model.PostLoginDestination
import eu.brrm.oblivio.ui.home.HomeRoute
import eu.brrm.oblivio.ui.notifications.NotificationPermissionRoute
import eu.brrm.oblivio.ui.register.RegisterRoute
import eu.brrm.oblivio.ui.signin.SignInRoute
import eu.brrm.oblivio.ui.splash.SplashRoute

@Composable
fun OblivioNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = OblivioDestination.Splash.route,
        modifier = modifier,
    ) {
        composable(OblivioDestination.Splash.route) {
            SplashRoute(
                onNavigate = { destination ->
                    navController.navigate(destination.route) {
                        popUpTo(OblivioDestination.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(OblivioDestination.SignIn.route) {
            SignInRoute(
                onNavigateToRegister = {
                    navController.navigate(OblivioDestination.Register.route)
                },
                onNavigateAfterSignIn = { destination ->
                    when (destination) {
                        PostLoginDestination.NOTIFICATION_PERMISSION ->
                            navController.navigate(OblivioDestination.NotificationPermission.route) {
                                popUpTo(OblivioDestination.SignIn.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        PostLoginDestination.HOME ->
                            navController.navigate(OblivioDestination.Home.route) {
                                popUpTo(OblivioDestination.SignIn.route) { inclusive = true }
                                launchSingleTop = true
                            }
                    }
                },
            )
        }
        composable(OblivioDestination.Register.route) {
            RegisterRoute(
                onNavigateAfterRegister = { destination ->
                    when (destination) {
                        PostLoginDestination.NOTIFICATION_PERMISSION ->
                            navController.navigate(OblivioDestination.NotificationPermission.route) {
                                popUpTo(OblivioDestination.SignIn.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        PostLoginDestination.HOME ->
                            navController.navigate(OblivioDestination.Home.route) {
                                popUpTo(OblivioDestination.SignIn.route) { inclusive = true }
                                launchSingleTop = true
                            }
                    }
                },
            )
        }
        composable(OblivioDestination.NotificationPermission.route) {
            NotificationPermissionRoute(
                onNavigateNext = {
                    navController.navigate(OblivioDestination.Home.route) {
                        popUpTo(OblivioDestination.NotificationPermission.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(OblivioDestination.Home.route) {
            HomeRoute(
                onProfileSignedOut = {
                    navController.navigate(OblivioDestination.SignIn.route) {
                        popUpTo(OblivioDestination.Home.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}
