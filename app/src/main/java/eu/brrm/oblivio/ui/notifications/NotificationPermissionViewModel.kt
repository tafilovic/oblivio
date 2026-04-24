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

    fun onIntent(intent: NotificationPermissionIntent) {
        when (intent) {
            NotificationPermissionIntent.EnableClicked -> requestPermission()
            is NotificationPermissionIntent.PermissionResult -> saveResult(intent.isGranted)
            NotificationPermissionIntent.MaybeLaterClicked -> saveResult(false)
        }
    }

    private fun requestPermission() {
        viewModelScope.launch {
            _effect.send(NotificationPermissionEffect.RequestSystemPermission)
        }
    }

    private fun saveResult(enabled: Boolean) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            notificationRepository.markPromptHandled(enabled)
            _state.value = _state.value.copy(isSaving = false)
            _effect.send(NotificationPermissionEffect.NavigateNext)
        }
    }

}
