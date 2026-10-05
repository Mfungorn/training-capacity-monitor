package com.fungorn.trainingcapacity.core.domain.usecase

import com.fungorn.trainingcapacity.core.domain.model.MesocycleStatistics
import com.fungorn.trainingcapacity.core.domain.repository.EntryRepository
import com.fungorn.trainingcapacity.core.domain.repository.MesocycleRepository
import com.fungorn.trainingcapacity.core.domain.statistics.MesocycleStatisticsCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetMesocycleStatisticsUseCase(
    private val mesocycleRepository: MesocycleRepository,
    private val entryRepository: EntryRepository
) {
    operator fun invoke(mesocycleId: String): Flow<MesocycleStatistics?> =
        mesocycleRepository.getCycleById(mesocycleId)
            .combine(entryRepository.getAllEntries()) { cycle, entries ->
                cycle?.let { MesocycleStatisticsCalculator.calculate(it, entries) }
            }
}
