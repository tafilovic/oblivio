package eu.brrm.oblivio.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeRoute(
    onProfileSignedOut: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.onIntent(HomeIntent.OnAppear)
    }
    HomeScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onProfileSignedOut = onProfileSignedOut,
    )
}
