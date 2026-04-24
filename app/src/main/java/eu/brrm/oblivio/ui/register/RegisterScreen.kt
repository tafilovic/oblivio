package eu.brrm.oblivio.ui.register

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.brrm.oblivio.R
import eu.brrm.oblivio.ui.AuthImeEnterExitAnim
import eu.brrm.oblivio.ui.isImeOpen
import eu.brrm.oblivio.ui.components.OblivioBackground
import eu.brrm.oblivio.ui.components.OblivioLogo
import eu.brrm.oblivio.ui.components.OblivioPrimaryButton
import eu.brrm.oblivio.ui.components.OblivioTextField
import eu.brrm.oblivio.ui.theme.OblivioTheme

@Composable
fun RegisterScreen(
    state: RegisterState,
    onIntent: (RegisterIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboardOpen = isImeOpen()
    val logoEnter = fadeIn(tween(AuthImeEnterExitAnim.durationInMs)) + expandVertically(
        animationSpec = tween(AuthImeEnterExitAnim.durationInMs),
        expandFrom = Alignment.Top,
    )
    val logoExit = fadeOut(tween(AuthImeEnterExitAnim.durationOutMs)) + shrinkVertically(
        animationSpec = tween(AuthImeEnterExitAnim.durationOutMs),
        shrinkTowards = Alignment.Top,
    )
    OblivioBackground(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 28.dp),
        ) {
            Column {
                if (keyboardOpen) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
                AnimatedVisibility(
                    visible = !keyboardOpen,
                    enter = logoEnter,
                    exit = logoExit,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.height(40.dp))
                        OblivioLogo(
                            wordmarkText = stringResource(id = R.string.app_wordmark),
                            modifier = Modifier.fillMaxWidth(0.7f),
                            markScale = 0.7f,
                        )
                        Spacer(modifier = Modifier.height(48.dp))
                    }
                }
                Text(
                    text = stringResource(id = R.string.register_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.height(20.dp))
                OblivioTextField(
                    value = state.username,
                    onValueChange = { onIntent(RegisterIntent.UsernameChanged(it)) },
                    label = stringResource(id = R.string.register_username_label),
                )
                Spacer(modifier = Modifier.height(12.dp))
                OblivioTextField(
                    value = state.email,
                    onValueChange = { onIntent(RegisterIntent.EmailChanged(it)) },
                    label = stringResource(id = R.string.register_email_label),
                )
                Spacer(modifier = Modifier.height(12.dp))
                OblivioTextField(
                    value = state.password,
                    onValueChange = { onIntent(RegisterIntent.PasswordChanged(it)) },
                    label = stringResource(id = R.string.register_password_label),
                    visualTransformation = if (state.isPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(onClick = { onIntent(RegisterIntent.TogglePasswordVisibility) }) {
                            Icon(
                                imageVector = if (state.isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = stringResource(id = R.string.register_toggle_password_content_desc),
                            )
                        }
                    },
                )
                Spacer(modifier = Modifier.height(12.dp))
                OblivioTextField(
                    value = state.confirmPassword,
                    onValueChange = { onIntent(RegisterIntent.ConfirmPasswordChanged(it)) },
                    label = stringResource(id = R.string.register_confirm_password_label),
                    visualTransformation = if (state.isConfirmPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(onClick = { onIntent(RegisterIntent.ToggleConfirmPasswordVisibility) }) {
                            Icon(
                                imageVector = if (state.isConfirmPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = stringResource(id = R.string.register_toggle_confirm_password_content_desc),
                            )
                        }
                    },
                )
                val errorLine = state.errorMessageText
                    ?: state.errorMessageResId?.let { stringResource(id = it) }
                if (errorLine != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorLine,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                OblivioPrimaryButton(
                    text = stringResource(id = R.string.register_action),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onIntent(RegisterIntent.Submit) },
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(id = R.string.footer_copyright),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterPreviewLight() {
    OblivioTheme {
        RegisterScreen(state = RegisterState(), onIntent = {})
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun RegisterPreviewDark() {
    OblivioTheme {
        RegisterScreen(state = RegisterState(), onIntent = {})
    }
}
