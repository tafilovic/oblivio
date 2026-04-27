package eu.brrm.oblivio.ui.notifications

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.brrm.oblivio.R
import eu.brrm.oblivio.ui.components.OblivioBackground
import eu.brrm.oblivio.ui.components.OblivioPrimaryButton
import eu.brrm.oblivio.ui.theme.OblivioTheme

@Composable
fun NotificationPermissionScreen(
    onEnableClick: () -> Unit,
    showSettingsDialog: Boolean,
    onSettingsDialogDismiss: () -> Unit,
    onOpenSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OblivioBackground(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Spacer(modifier = Modifier.height(96.dp))
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
                Spacer(modifier = Modifier.height(96.dp))
                Text(
                    text = stringResource(id = R.string.notifications_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(id = R.string.notifications_body),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f),
                )
                Spacer(modifier = Modifier.height(24.dp))
                OblivioPrimaryButton(
                    text = stringResource(id = R.string.notifications_enable_action),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onEnableClick,
                )
            }
            Text(
                text = stringResource(id = R.string.footer_copyright),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = onSettingsDialogDismiss,
            title = {
                Text(text = stringResource(id = R.string.notifications_settings_dialog_title))
            },
            text = {
                Text(text = stringResource(id = R.string.notifications_settings_dialog_message))
            },
            confirmButton = {
                TextButton(onClick = onOpenSettingsClick) {
                    Text(text = stringResource(id = R.string.notifications_settings_dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = onSettingsDialogDismiss) {
                    Text(text = stringResource(id = R.string.notifications_settings_dialog_cancel))
                }
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationPermissionPreviewLight() {
    OblivioTheme {
        NotificationPermissionScreen(
            onEnableClick = {},
            showSettingsDialog = false,
            onSettingsDialogDismiss = {},
            onOpenSettingsClick = {},
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NotificationPermissionPreviewDark() {
    OblivioTheme {
        NotificationPermissionScreen(
            onEnableClick = {},
            showSettingsDialog = true,
            onSettingsDialogDismiss = {},
            onOpenSettingsClick = {},
        )
    }
}
