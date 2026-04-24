package eu.brrm.oblivio.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.brrm.oblivio.domain.model.AppThemeMode
import eu.brrm.oblivio.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class AppThemeViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    val appThemeMode = userPreferencesRepository.appThemeMode
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            AppThemeMode.System,
        )
}
