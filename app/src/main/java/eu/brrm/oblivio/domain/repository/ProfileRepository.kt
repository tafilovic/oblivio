package eu.brrm.oblivio.domain.repository

import eu.brrm.oblivio.domain.model.ProfileSummary

interface ProfileRepository {
    suspend fun loadProfile(): ProfileSummary
}
