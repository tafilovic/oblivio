package eu.brrm.oblivio.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity

/**
 * True when the soft keyboard is open (IME bottom inset is non-zero).
 * Uses window insets; recomposition tracks IME show/hide with `imePadding()` in the same hierarchy.
 */
@Composable
fun isImeOpen(): Boolean {
    val density = LocalDensity.current
    return WindowInsets.ime.getBottom(density) > 0
}

/** Durations (ms) for auth screens logo show/hide with IME. */
object AuthImeEnterExitAnim {
    const val durationInMs = 240
    const val durationOutMs = 200
}
