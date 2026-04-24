package eu.brrm.oblivio.data.repository

import eu.brrm.oblivio.domain.model.HomeSummary
import eu.brrm.oblivio.domain.repository.HomeRepository
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor() : HomeRepository {
    override suspend fun loadHomeSummary(): HomeSummary = HomeSummary(isSessionActive = true)
}
