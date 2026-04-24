package eu.brrm.oblivio.ui.home

import androidx.annotation.StringRes

data class HomeState(
    val selectedTab: HomeTab = HomeTab.Notifications,
    val isSessionActive: Boolean = false,
    val isLoading: Boolean = true,
    @StringRes val errorMessageResId: Int? = null,
)

enum class HomeTab {
    Notifications,
    Profile,
}

sealed interface HomeIntent {
    data object OnAppear : HomeIntent
    data class SelectTab(val tab: HomeTab) : HomeIntent
}
