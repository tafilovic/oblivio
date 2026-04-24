package eu.brrm.oblivio.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.brrm.oblivio.R
import eu.brrm.oblivio.domain.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.OnAppear -> load()
            is HomeIntent.SelectTab -> {
                _state.value = _state.value.copy(
                    selectedTab = intent.tab,
                    errorMessageResId = null,
                )
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                errorMessageResId = null,
            )
            val summary = runCatching { homeRepository.loadHomeSummary() }
            summary
                .onSuccess {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isSessionActive = it.isSessionActive,
                    )
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isSessionActive = false,
                        errorMessageResId = R.string.error_home_load_failed,
                    )
                }
        }
    }
}
