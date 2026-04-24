package eu.brrm.oblivio.domain.model

/**
 * Minimal home payload for the signed-in experience.
 */
data class HomeSummary(
    val isSessionActive: Boolean = true,
)
