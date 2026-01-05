package com.fungorn.trainingcapacity.core.domain.usecase

import com.fungorn.trainingcapacity.core.domain.model.TrainingMesocycle
import com.fungorn.trainingcapacity.core.domain.repository.MesocycleRepository

class StartNewMesocycleUseCase(
    private val repository: MesocycleRepository
) {
    suspend operator fun invoke(cycle: TrainingMesocycle) = repository.createCycle(cycle)
}