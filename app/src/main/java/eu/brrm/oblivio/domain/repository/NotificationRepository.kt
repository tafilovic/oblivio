package eu.brrm.oblivio.domain.repository

import eu.brrm.oblivio.domain.model.PostLoginDestination

interface NotificationRepository {
    suspend fun markPromptHandled(enabled: Boolean)
    suspend fun isPromptHandled(): Boolean
    suspend fun markPermissionRequestAttempted()
    suspend fun wasPermissionRequestAttempted(): Boolean
    suspend fun subscribeCurrentDevice(): Result<Unit>

    /**
     * After sign-in, either show the notification permission flow or go straight to home.
     */
    suspend fun resolvePostLoginDestination(): PostLoginDestination
}
