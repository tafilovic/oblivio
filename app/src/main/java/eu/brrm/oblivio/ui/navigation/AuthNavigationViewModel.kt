package eu.brrm.oblivio.ui.navigation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.brrm.oblivio.data.remote.AuthLogoutNotifier
import javax.inject.Inject

@HiltViewModel
class AuthNavigationViewModel @Inject constructor(
    logoutNotifier: AuthLogoutNotifier,
) : ViewModel() {
    val logoutEvents = logoutNotifier.events
}
