package eu.brrm.oblivio.ui.register

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.brrm.oblivio.domain.model.PostLoginDestination
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RegisterRoute(
    onNavigateAfterRegister: (PostLoginDestination) -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is RegisterEffect.NavigateAfterRegister -> onNavigateAfterRegister(effect.destination)
            }
        }
    }
    RegisterScreen(
        state = state,
        onIntent = viewModel::onIntent,
    )
}
