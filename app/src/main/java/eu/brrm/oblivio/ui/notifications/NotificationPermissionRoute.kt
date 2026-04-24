package eu.brrm.oblivio.ui.notifications

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NotificationPermissionRoute(
    onNavigateNext: () -> Unit,
    viewModel: NotificationPermissionViewModel = hiltViewModel(),
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        viewModel.onIntent(NotificationPermissionIntent.PermissionResult(isGranted = isGranted))
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                NotificationPermissionEffect.NavigateNext -> onNavigateNext()
                NotificationPermissionEffect.RequestSystemPermission -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        viewModel.onIntent(NotificationPermissionIntent.PermissionResult(isGranted = true))
                    }
                }
            }
        }
    }

    NotificationPermissionScreen(
        onEnableClick = { viewModel.onIntent(NotificationPermissionIntent.EnableClicked) },
        onMaybeLaterClick = { viewModel.onIntent(NotificationPermissionIntent.MaybeLaterClicked) },
    )
}
