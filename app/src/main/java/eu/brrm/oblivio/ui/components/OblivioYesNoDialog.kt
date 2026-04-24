package eu.brrm.oblivio.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.remember
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import eu.brrm.oblivio.ui.theme.OblivioTheme

/**
 * Modal confirmation with two actions, styled for Oblivio screens.
 *
 * - [onNegative] and [onPositive] are called for the “no” and “yes” actions.
 * - [onDismissRequest] is invoked for system back and scrim; typically match [onNegative].
 * - Use [OblivioDialogConfirmStyle.Destructive] for irreversible or risky “yes” (e.g. log out, delete).
 */
@Composable
fun OblivioYesNoDialog(
    title: String,
    message: String,
    noText: String,
    yesText: String,
    onDismissRequest: () -> Unit,
    onNegative: () -> Unit,
    onPositive: () -> Unit,
    modifier: Modifier = Modifier,
    confirmStyle: OblivioDialogConfirmStyle = OblivioDialogConfirmStyle.Default,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = modifier
                .widthIn(max = 400.dp)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            shadowElevation = 6.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 20.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = message,
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 22.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
                ) {
                    OblivioSecondaryButton(
                        text = noText,
                        onClick = onNegative,
                        modifier = Modifier.widthIn(min = 112.dp),
                    )
                    when (confirmStyle) {
                        OblivioDialogConfirmStyle.Default -> OblivioPrimaryButton(
                            text = yesText,
                            onClick = onPositive,
                            modifier = Modifier.widthIn(min = 112.dp),
                        )
                        OblivioDialogConfirmStyle.Destructive -> OblivioDialogDestructiveConfirmButton(
                            text = yesText,
                            onClick = onPositive,
                            modifier = Modifier.widthIn(min = 112.dp),
                        )
                    }
                }
            }
        }
    }
}

enum class OblivioDialogConfirmStyle {
    /** “Yes” uses the copper/bronze gradient (typical positive completion). */
    Default,

    /** “Yes” is outlined in error tone (e.g. log out, delete). */
    Destructive,
}

@Composable
private fun OblivioDialogDestructiveConfirmButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val error = MaterialTheme.colorScheme.error
    val surface = remember(error) { error.copy(alpha = 0.12f) }
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = surface,
            contentColor = error,
        ),
        border = BorderStroke(1.dp, error),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

@Preview(showBackground = true)
@Composable
private fun OblivioYesNoDialogDefaultPreview() {
    OblivioTheme(darkTheme = false) {
        OblivioYesNoDialog(
            title = "Continue?",
            message = "This will apply your changes.",
            noText = "No",
            yesText = "Yes",
            onDismissRequest = {},
            onNegative = {},
            onPositive = {},
            confirmStyle = OblivioDialogConfirmStyle.Default,
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun OblivioYesNoDialogDestructivePreview() {
    OblivioTheme(darkTheme = true) {
        OblivioYesNoDialog(
            title = "Log out?",
            message = "You will need to sign in again.",
            noText = "Stay signed in",
            yesText = "Log out",
            onDismissRequest = {},
            onNegative = {},
            onPositive = {},
            confirmStyle = OblivioDialogConfirmStyle.Destructive,
        )
    }
}
