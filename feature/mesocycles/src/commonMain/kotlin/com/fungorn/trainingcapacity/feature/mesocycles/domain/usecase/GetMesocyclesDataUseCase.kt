package com.fungorn.trainingcapacity.feature.mesocycles.domain.usecase

import com.fungorn.trainingcapacity.core.common.formatMillisToDate
import com.fungorn.trainingcapacity.core.domain.repository.MesocycleRepository
import com.fungorn.trainingcapacity.feature.mesocycles.domain.model.MesocycleItem
import com.fungorn.trainingcapacity.feature.mesocycles.domain.model.MesocyclesData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetMesocyclesDataUseCase(
    private val repository: MesocycleRepository
) {
    operator fun invoke(): Flow<MesocyclesData> =
        repository.getAllCycles().map { cycles ->
            var currentSelectedMesocycle: MesocycleItem? = null
            val previousMesocycles = mutableListOf<MesocycleItem>()
            cycles.forEach { cycle ->
                MesocycleItem(
                    id = cycle.id,
                    title = "${cycle.weeks.size}-week cycle",
                    startDateFormatted = formatMillisToDate(cycle.startedAt),
                    isSelected = cycle.isSelected
                ).also {
                    if (cycle.isSelected) {
                        currentSelectedMesocycle = it
                    } else {
                        previousMesocycles.add(it)
                    }
                }
            }
            MesocyclesData(currentSelectedMesocycle, previousMesocycles)
        }
}
