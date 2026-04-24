package eu.brrm.oblivio.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.brrm.oblivio.R
import eu.brrm.oblivio.ui.components.OblivioBackground
import eu.brrm.oblivio.ui.profile.ProfileRoute
import eu.brrm.oblivio.ui.theme.BrandBronze
import eu.brrm.oblivio.ui.theme.OblivioTheme

@Composable
fun HomeScreen(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
    onProfileSignedOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar {
                val notificationsLabel = stringResource(R.string.home_nav_notifications)
                val profileLabel = stringResource(R.string.home_nav_profile)
                val colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BrandBronze,
                    selectedTextColor = BrandBronze,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                )
                NavigationBarItem(
                    selected = state.selectedTab == HomeTab.Notifications,
                    onClick = { onIntent(HomeIntent.SelectTab(HomeTab.Notifications)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = notificationsLabel,
                        )
                    },
                    label = { Text(notificationsLabel) },
                    colors = colors,
                )
                NavigationBarItem(
                    selected = state.selectedTab == HomeTab.Profile,
                    onClick = { onIntent(HomeIntent.SelectTab(HomeTab.Profile)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = profileLabel,
                        )
                    },
                    label = { Text(profileLabel) },
                    colors = colors,
                )
            }
        },
    ) { padding ->
        OblivioBackground(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
            ) {
                if (state.selectedTab == HomeTab.Notifications) {
                    Text(
                        text = stringResource(R.string.home_section_notifications_title),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                if (state.isLoading) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    val error = state.errorMessageResId
                    if (error != null) {
                        Text(
                            text = stringResource(error),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                        )
                    } else {
                        when (state.selectedTab) {
                            HomeTab.Notifications -> NotificationsTabContent(
                                modifier = Modifier.weight(1f),
                            )

                            HomeTab.Profile -> ProfileRoute(
                                onSignedOut = onProfileSignedOut,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationsTabContent(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.home_welcome_title),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.home_welcome_body),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f),
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreviewLight() {
    OblivioTheme {
        HomeScreen(
            state = HomeState(
                isSessionActive = true,
                isLoading = false,
            ),
            onIntent = {},
            onProfileSignedOut = {},
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeScreenPreviewDark() {
    OblivioTheme {
        HomeScreen(
            state = HomeState(
                isSessionActive = true,
                isLoading = false,
                selectedTab = HomeTab.Profile,
            ),
            onIntent = {},
            onProfileSignedOut = {},
        )
    }
}
