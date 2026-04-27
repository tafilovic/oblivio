package eu.brrm.oblivio.domain.repository

import eu.brrm.oblivio.domain.model.PostLoginDestination

interface NotificationRepository {
    suspend fun markPromptHandled(enabled: Boolean)
    suspend fun isPromptHandled(): Boolean
    suspend fun subscribeCurrentDevice(): Result<Unit>

    /**
     * After sign-in, either show the notification onboarding flow or go straight to home
     * (already granted, or user completed onboarding, or not applicable on older APIs is handled in impl).
     */
    suspend fun resolvePostLoginDestination(): PostLoginDestination
}
