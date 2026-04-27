package eu.brrm.oblivio.ui.notifications

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NotificationPermissionRoute(
    onNavigateNext: () -> Unit,
    viewModel: NotificationPermissionViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val latestState by rememberUpdatedState(state)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        viewModel.onIntent(NotificationPermissionIntent.PermissionResult(isGranted = isGranted))
    }

    DisposableEffect(context, lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                when {
                    context.hasNotificationPermission() ->
                        viewModel.onIntent(
                            NotificationPermissionIntent.PermissionResult(isGranted = true),
                        )
                    latestState.hasOpenedSettings ->
                        viewModel.onIntent(
                            NotificationPermissionIntent.PermissionResult(isGranted = false),
                        )
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                NotificationPermissionEffect.NavigateNext -> onNavigateNext()
                NotificationPermissionEffect.CloseApp -> activity?.finishAffinity()
                NotificationPermissionEffect.OpenNotificationSettings ->
                    context.openNotificationSettings()
                NotificationPermissionEffect.RequestSystemPermission -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val canRequestSystemPermission =
                            activity != null &&
                                (!latestState.hasRequestedSystemPermission ||
                                    ActivityCompat.shouldShowRequestPermissionRationale(
                                        activity,
                                        Manifest.permission.POST_NOTIFICATIONS,
                                    ))
                        if (canRequestSystemPermission) {
                            viewModel.onIntent(
                                NotificationPermissionIntent.SystemPermissionRequestStarted,
                            )
                            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            viewModel.onIntent(
                                NotificationPermissionIntent.SystemPermissionUnavailable,
                            )
                        }
                    } else {
                        viewModel.onIntent(NotificationPermissionIntent.PermissionResult(isGranted = true))
                    }
                }
            }
        }
    }

    BackHandler {
        activity?.finishAffinity()
    }

    NotificationPermissionScreen(
        onEnableClick = { viewModel.onIntent(NotificationPermissionIntent.EnableClicked) },
        showSettingsDialog = state.showSettingsDialog,
        onSettingsDialogDismiss = {
            viewModel.onIntent(NotificationPermissionIntent.SettingsDialogDismissed)
        },
        onOpenSettingsClick = {
            viewModel.onIntent(NotificationPermissionIntent.OpenSettingsClicked)
        },
    )
}

private tailrec fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}

private fun Context.openNotificationSettings() {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
    startActivity(intent)
}

private fun Context.hasNotificationPermission(): Boolean {
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
}
