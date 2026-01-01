package com.fungorn.trainingcapacity.core.domain.usecase

import com.fungorn.trainingcapacity.core.domain.model.TrainingMesocycle
import com.fungorn.trainingcapacity.core.domain.repository.TrainingRepository

class StartNewMesocycleUseCase(
    private val repository: TrainingRepository
) {
    suspend operator fun invoke(cycle: TrainingMesocycle) = repository.createCycle(cycle)
}