package eu.brrm.oblivio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import eu.brrm.oblivio.domain.model.AppThemeMode
import eu.brrm.oblivio.ui.main.AppThemeViewModel
import eu.brrm.oblivio.ui.navigation.OblivioNavHost
import eu.brrm.oblivio.ui.theme.OblivioTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appThemeViewModel: AppThemeViewModel = hiltViewModel()
            val themeMode by appThemeViewModel.appThemeMode.collectAsStateWithLifecycle()
            val isSystemDark = isSystemInDarkTheme()
            val useDark = when (themeMode) {
                AppThemeMode.Light -> false
                AppThemeMode.Dark -> true
                AppThemeMode.System -> isSystemDark
            }
            OblivioTheme(darkTheme = useDark) {
                Scaffold { innerPadding ->
                    val navController = rememberNavController()
                    OblivioNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}
