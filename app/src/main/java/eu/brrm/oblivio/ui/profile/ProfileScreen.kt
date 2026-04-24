package eu.brrm.oblivio.ui.profile

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eu.brrm.oblivio.R
import eu.brrm.oblivio.domain.model.AppThemeMode
import eu.brrm.oblivio.ui.components.OblivioDialogConfirmStyle
import eu.brrm.oblivio.ui.components.OblivioYesNoDialog
import eu.brrm.oblivio.ui.theme.BrandBronze
import eu.brrm.oblivio.ui.theme.BrandIvory
import eu.brrm.oblivio.ui.theme.OblivioProfileTitleTextStyle
import eu.brrm.oblivio.ui.theme.OblivioTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ProfileScreen(
    state: ProfileState,
    onIntent: (ProfileIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .defaultMinSize(minHeight = 200.dp),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    val err = state.errorMessageResId
    if (err != null) {
        Text(
            text = stringResource(err),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge,
            modifier = modifier.padding(16.dp),
        )
        return
    }

    val scroll = rememberScrollState()
    val isSystemDark = isSystemInDarkTheme()
    val lightRowSelected = state.appTheme == AppThemeMode.Light ||
        (state.appTheme == AppThemeMode.System && !isSystemDark)
    val darkRowSelected = state.appTheme == AppThemeMode.Dark ||
        (state.appTheme == AppThemeMode.System && isSystemDark)
    val outline = MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)
    val onBg = MaterialTheme.colorScheme.onBackground
    val hint = onBg.copy(alpha = 0.6f)
    val switchColors = SwitchDefaults.colors(
        checkedTrackColor = BrandBronze,
        checkedThumbColor = BrandIvory,
        checkedBorderColor = Color.Transparent,
        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
    )
    val logoutError = MaterialTheme.colorScheme.error
    val logoutSurface = remember(logoutError) { logoutError.copy(alpha = 0.12f) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(bottom = 32.dp),
        ) {
        Text(
            text = stringResource(R.string.profile_title),
            style = OblivioProfileTitleTextStyle,
            color = onBg,
        )
        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            ConcentricFrameAvatar(
                size = 128.dp,
                borderColor = outline,
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = state.displayName,
            color = onBg,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(28.dp))
        Text(
            text = stringResource(R.string.profile_push_section),
            color = hint,
            style = MaterialTheme.typography.labelLarge,
        )
        Spacer(Modifier.height(6.dp))
        SwitchRow(
            label = stringResource(R.string.profile_push_chat),
            checked = state.pushChat,
            onCheckedChange = { onIntent(ProfileIntent.PushChatChanged(it)) },
            switchColors = switchColors,
        )
        SwitchRow(
            label = stringResource(R.string.profile_push_email),
            checked = state.pushEmail,
            onCheckedChange = { onIntent(ProfileIntent.PushEmailChanged(it)) },
            switchColors = switchColors,
        )
        SwitchRow(
            label = stringResource(R.string.profile_push_call),
            checked = state.pushCall,
            onCheckedChange = { onIntent(ProfileIntent.PushCallChanged(it)) },
            switchColors = switchColors,
        )
        Spacer(Modifier.height(8.dp))
        HorizontalDivider(color = outline.copy(alpha = 0.35f))
        Spacer(Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.profile_theme_section),
            color = hint,
            style = MaterialTheme.typography.labelLarge,
        )
        Spacer(Modifier.height(6.dp))
        ThemeCheckRow(
            label = stringResource(R.string.profile_theme_light),
            selected = lightRowSelected,
            onClick = { onIntent(ProfileIntent.ThemeSelected(AppThemeMode.Light)) },
        )
        HorizontalDivider(
            color = outline.copy(alpha = 0.2f),
            modifier = Modifier.padding(vertical = 2.dp),
        )
        ThemeCheckRow(
            label = stringResource(R.string.profile_theme_dark),
            selected = darkRowSelected,
            onClick = { onIntent(ProfileIntent.ThemeSelected(AppThemeMode.Dark)) },
        )
        HorizontalDivider(
            color = outline.copy(alpha = 0.2f),
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.profile_account_section),
            color = hint,
            style = MaterialTheme.typography.labelLarge,
        )
        Spacer(Modifier.height(10.dp))
        OutlinedButton(
            onClick = { onIntent(ProfileIntent.LogoutClicked) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            border = BorderStroke(1.dp, logoutError),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = logoutSurface,
                contentColor = logoutError,
            ),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = logoutError,
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.profile_logout),
                style = MaterialTheme.typography.labelLarge,
                color = logoutError,
            )
        }
        }
        if (state.showLogoutDialog) {
            OblivioYesNoDialog(
                title = stringResource(R.string.profile_logout_dialog_title),
                message = stringResource(R.string.profile_logout_dialog_message),
                noText = stringResource(R.string.profile_logout_dialog_cancel),
                yesText = stringResource(R.string.profile_logout_dialog_confirm),
                onDismissRequest = { onIntent(ProfileIntent.LogoutDialogDismissed) },
                onNegative = { onIntent(ProfileIntent.LogoutDialogDismissed) },
                onPositive = { onIntent(ProfileIntent.LogoutDialogConfirmed) },
                confirmStyle = OblivioDialogConfirmStyle.Destructive,
            )
        }
    }
}

@Composable
private fun ConcentricFrameAvatar(
    size: Dp,
    borderColor: Color,
) {
    val corner = 14.dp
    val step = 3.dp
    val stroke = 1.dp
    val onIcon = MaterialTheme.colorScheme.onBackground
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(size),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(corner))
                .border(stroke, borderColor, RoundedCornerShape(corner))
                .padding(step),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .border(stroke, borderColor, RoundedCornerShape(12.dp))
                    .padding(step),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                        .border(stroke, borderColor, RoundedCornerShape(10.dp))
                        .padding(step),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                            .border(stroke, borderColor, RoundedCornerShape(8.dp))
                            .background(BrandIvory.copy(alpha = 0.1f))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = stringResource(R.string.profile_avatar_content_desc),
                            modifier = Modifier.size(56.dp),
                            tint = onIcon,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    switchColors: SwitchColors,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = switchColors,
        )
    }
}

@Composable
private fun ThemeCheckRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val onBg = MaterialTheme.colorScheme.onBackground
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = onBg,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        if (selected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = onBg,
            )
        } else {
            Spacer(Modifier.size(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreviewLight() {
    OblivioTheme(darkTheme = false) {
        ProfileScreen(
            state = ProfileState(
                displayName = "MariaEnneNoir335",
                pushChat = true,
                pushEmail = true,
                pushCall = true,
                appTheme = AppThemeMode.Light,
                isLoading = false,
            ),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProfileScreenPreviewDark() {
    OblivioTheme(darkTheme = true) {
        ProfileScreen(
            state = ProfileState(
                displayName = "MariaEnneNoir335",
                isLoading = false,
                appTheme = AppThemeMode.Dark,
            ),
            onIntent = {},
        )
    }
}
