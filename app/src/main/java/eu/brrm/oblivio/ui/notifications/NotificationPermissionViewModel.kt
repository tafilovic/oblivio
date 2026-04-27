package eu.brrm.oblivio.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.brrm.oblivio.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class NotificationPermissionViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(NotificationPermissionState())
    val state: StateFlow<NotificationPermissionState> = _state.asStateFlow()

    private val _effect = Channel<NotificationPermissionEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                hasRequestedSystemPermission =
                    notificationRepository.wasPermissionRequestAttempted(),
            )
        }
    }

    fun onIntent(intent: NotificationPermissionIntent) {
        when (intent) {
            NotificationPermissionIntent.EnableClicked -> requestPermission()
            NotificationPermissionIntent.SystemPermissionRequestStarted ->
                markPermissionRequestAttempted()
            NotificationPermissionIntent.SystemPermissionUnavailable -> showSettingsDialog()
            is NotificationPermissionIntent.PermissionResult -> handlePermissionResult(intent.isGranted)
            NotificationPermissionIntent.SettingsDialogDismissed -> closeApp()
            NotificationPermissionIntent.OpenSettingsClicked -> openSettings()
        }
    }

    private fun requestPermission() {
        viewModelScope.launch {
            _effect.send(NotificationPermissionEffect.RequestSystemPermission)
        }
    }

    private fun markPermissionRequestAttempted() {
        viewModelScope.launch {
            notificationRepository.markPermissionRequestAttempted()
            _state.value = _state.value.copy(hasRequestedSystemPermission = true)
        }
    }

    private fun showSettingsDialog() {
        _state.value = _state.value.copy(showSettingsDialog = true)
    }

    private fun openSettings() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                showSettingsDialog = false,
                hasOpenedSettings = true,
            )
            _effect.send(NotificationPermissionEffect.OpenNotificationSettings)
        }
    }

    private fun handlePermissionResult(isGranted: Boolean) {
        if (isGranted) {
            saveGrant()
        } else {
            closeApp()
        }
    }

    private fun saveGrant() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            notificationRepository.markPromptHandled(enabled = true)
            _state.value = _state.value.copy(isSaving = false)
            _effect.send(NotificationPermissionEffect.NavigateNext)
        }
    }

    private fun closeApp() {
        viewModelScope.launch {
            _state.value = _state.value.copy(showSettingsDialog = false)
            _effect.send(NotificationPermissionEffect.CloseApp)
        }
    }

}
