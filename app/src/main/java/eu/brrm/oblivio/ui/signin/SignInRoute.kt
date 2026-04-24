package eu.brrm.oblivio.ui.signin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.runtime.LaunchedEffect
import eu.brrm.oblivio.domain.model.PostLoginDestination

@Composable
fun SignInRoute(
    onNavigateToRegister: () -> Unit,
    onNavigateAfterSignIn: (PostLoginDestination) -> Unit,
    viewModel: SignInViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                SignInEffect.NavigateToRegister -> onNavigateToRegister()
                is SignInEffect.NavigateAfterSignIn -> onNavigateAfterSignIn(effect.destination)
            }
        }
    }
    SignInScreen(
        state = state,
        onIntent = viewModel::onIntent,
    )
}
