package com.fungorn.trainingcapacity.feature.mesocycles.domain.usecase

import com.fungorn.trainingcapacity.core.common.formatMillisToDate
import com.fungorn.trainingcapacity.core.domain.repository.MesocycleRepository
import com.fungorn.trainingcapacity.feature.mesocycles.domain.model.MesocycleItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetMesocycleListUseCase(
    private val repository: MesocycleRepository
) {
    operator fun invoke(): Flow<List<MesocycleItem>> =
        repository.getAllCycles().map { cycles ->
            cycles.map { cycle ->
                MesocycleItem(
                    id = cycle.id,
                    title = "${cycle.weeks.size}-week cycle",
                    startDateFormatted = formatMillisToDate(cycle.startedAt),
                    isSelected = cycle.isSelected
                )
            }
        }
}
