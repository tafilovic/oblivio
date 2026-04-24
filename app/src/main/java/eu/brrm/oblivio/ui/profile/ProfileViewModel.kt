package eu.brrm.oblivio.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.brrm.oblivio.R
import eu.brrm.oblivio.domain.model.AppThemeMode
import eu.brrm.oblivio.domain.repository.AuthRepository
import eu.brrm.oblivio.domain.repository.ProfileRepository
import eu.brrm.oblivio.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    private var profileLoaded = false

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private val _effect = Channel<ProfileEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            combine(
                userPreferencesRepository.appThemeMode,
                userPreferencesRepository.pushChatEnabled,
                userPreferencesRepository.pushEmailEnabled,
                userPreferencesRepository.pushCallEnabled,
            ) { theme, c, e, cl ->
                ProfilePrefsSnapshot(theme, c, e, cl)
            }.collect { snap ->
                _state.update { s ->
                    s.copy(
                        appTheme = snap.theme,
                        pushChat = snap.pushChat,
                        pushEmail = snap.pushEmail,
                        pushCall = snap.pushCall,
                    )
                }
            }
        }
    }

    private data class ProfilePrefsSnapshot(
        val theme: AppThemeMode,
        val pushChat: Boolean,
        val pushEmail: Boolean,
        val pushCall: Boolean,
    )

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.OnAppear -> onAppear()
            is ProfileIntent.PushChatChanged -> viewModelScope.launch {
                userPreferencesRepository.setPushChatEnabled(intent.value)
            }
            is ProfileIntent.PushEmailChanged -> viewModelScope.launch {
                userPreferencesRepository.setPushEmailEnabled(intent.value)
            }
            is ProfileIntent.PushCallChanged -> viewModelScope.launch {
                userPreferencesRepository.setPushCallEnabled(intent.value)
            }
            is ProfileIntent.ThemeSelected -> viewModelScope.launch {
                userPreferencesRepository.setAppThemeMode(intent.mode)
            }
            ProfileIntent.LogoutClicked -> _state.update { it.copy(showLogoutDialog = true) }
            ProfileIntent.LogoutDialogDismissed -> _state.update { it.copy(showLogoutDialog = false) }
            ProfileIntent.LogoutDialogConfirmed -> {
                _state.update { it.copy(showLogoutDialog = false) }
                logout()
            }
        }
    }

    private fun onAppear() {
        if (profileLoaded) return
        profileLoaded = true
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessageResId = null) }
            runCatching { profileRepository.loadProfile() }
                .onSuccess { p ->
                    _state.update { it.copy(displayName = p.displayName, isLoading = false) }
                }
                .onFailure {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessageResId = R.string.error_profile_load,
                        )
                    }
                }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            runCatching { authRepository.signOut() }
            _effect.send(ProfileEffect.NavigateToSignIn)
        }
    }
}
