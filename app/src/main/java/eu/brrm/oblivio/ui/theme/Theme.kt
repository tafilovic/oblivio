package eu.brrm.oblivio.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = DarkButtonSolid,
    onPrimary = DarkButtonOnSolid,
    secondary = BrandBronze,
    tertiary = BrandCopper,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    outline = DarkOutline,
)

private val LightColorScheme = lightColorScheme(
    primary = LightButtonSolid,
    onPrimary = LightButtonOnSolid,
    secondary = BrandBronze,
    tertiary = BrandCopper,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    outline = LightOutline,
)

@Composable
fun OblivioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = OblivioTypography,
        content = content
    )
}