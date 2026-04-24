package eu.brrm.oblivio.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.brrm.oblivio.R
import eu.brrm.oblivio.ui.theme.OblivioTheme

// region OblivioBackground
@Preview(name = "Background Light", showBackground = true)
@Composable
private fun OblivioBackgroundPreviewLight() {
    OblivioTheme(darkTheme = false) {
        OblivioBackground(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.app_name),
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}

@Preview(name = "Background Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun OblivioBackgroundPreviewDark() {
    OblivioTheme(darkTheme = true) {
        OblivioBackground(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.app_name),
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}
// endregion

// region OblivioLogo
@Preview(name = "Logo + wordmark Light", showBackground = true)
@Composable
private fun OblivioLogoWithWordmarkPreviewLight() {
    OblivioTheme(darkTheme = false) {
        Box(Modifier.fillMaxWidth().padding(16.dp)) {
            OblivioLogo(
                wordmarkText = stringResource(R.string.app_wordmark),
                modifier = Modifier.fillMaxWidth(),
                includeWordmark = true,
            )
        }
    }
}

@Preview(name = "Logo + wordmark Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun OblivioLogoWithWordmarkPreviewDark() {
    OblivioTheme(darkTheme = true) {
        Box(Modifier.fillMaxWidth().padding(16.dp)) {
            OblivioLogo(
                wordmarkText = stringResource(R.string.app_wordmark),
                modifier = Modifier.fillMaxWidth(),
                includeWordmark = true,
            )
        }
    }
}

@Preview(name = "Logo mark only Light", showBackground = true)
@Composable
private fun OblivioLogoMarkOnlyPreviewLight() {
    OblivioTheme(darkTheme = false) {
        OblivioLogo(
            wordmarkText = stringResource(R.string.app_wordmark),
            includeWordmark = false,
        )
    }
}

@Preview(name = "Logo mark only Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun OblivioLogoMarkOnlyPreviewDark() {
    OblivioTheme(darkTheme = true) {
        OblivioLogo(
            wordmarkText = stringResource(R.string.app_wordmark),
            includeWordmark = false,
        )
    }
}
// endregion

// region Buttons
@Preview(name = "Primary Light", showBackground = true)
@Composable
private fun OblivioPrimaryButtonPreviewLight() {
    OblivioTheme(darkTheme = false) {
        OblivioPrimaryButton(
            text = stringResource(R.string.sign_in_action),
            onClick = {},
        )
    }
}

@Preview(name = "Primary Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun OblivioPrimaryButtonPreviewDark() {
    OblivioTheme(darkTheme = true) {
        OblivioPrimaryButton(
            text = stringResource(R.string.sign_in_action),
            onClick = {},
        )
    }
}

@Preview(name = "Secondary Light", showBackground = true)
@Composable
private fun OblivioSecondaryButtonPreviewLight() {
    OblivioTheme(darkTheme = false) {
        OblivioSecondaryButton(
            text = stringResource(R.string.sign_in_register_action),
            onClick = {},
        )
    }
}

@Preview(name = "Secondary Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun OblivioSecondaryButtonPreviewDark() {
    OblivioTheme(darkTheme = true) {
        OblivioSecondaryButton(
            text = stringResource(R.string.sign_in_register_action),
            onClick = {},
        )
    }
}
// endregion

// region TextField
@Preview(name = "TextField Light", showBackground = true)
@Composable
private fun OblivioTextFieldPreviewLight() {
    OblivioTheme(darkTheme = false) {
        OblivioTextField(
            value = "",
            onValueChange = {},
            label = stringResource(R.string.sign_in_username_label),
        )
    }
}

@Preview(name = "TextField Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun OblivioTextFieldPreviewDark() {
    OblivioTheme(darkTheme = true) {
        OblivioTextField(
            value = stringResource(R.string.app_name),
            onValueChange = {},
            label = stringResource(R.string.sign_in_username_label),
        )
    }
}

@Preview(name = "TextField + trailing Light", showBackground = true)
@Composable
private fun OblivioTextFieldTrailingPreviewLight() {
    OblivioTheme(darkTheme = false) {
        OblivioTextField(
            value = "",
            onValueChange = {},
            label = stringResource(R.string.sign_in_password_label),
            trailingIcon = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = stringResource(R.string.sign_in_toggle_password_content_desc),
                    )
                }
            },
        )
    }
}

@Preview(name = "TextField + trailing Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun OblivioTextFieldTrailingPreviewDark() {
    OblivioTheme(darkTheme = true) {
        OblivioTextField(
            value = "",
            onValueChange = {},
            label = stringResource(R.string.sign_in_password_label),
            trailingIcon = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = stringResource(R.string.sign_in_toggle_password_content_desc),
                    )
                }
            },
        )
    }
}
// endregion
