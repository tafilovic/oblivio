package eu.brrm.oblivio.domain.repository

import eu.brrm.oblivio.domain.model.HomeSummary

/**
 * App shell / home data (stubs for now; extend when backend is available).
 */
interface HomeRepository {
    suspend fun loadHomeSummary(): HomeSummary
}
