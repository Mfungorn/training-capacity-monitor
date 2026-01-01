package com.fungorn.trainingcapacity.core.domain.usecase

import com.fungorn.trainingcapacity.core.domain.repository.TrainingRepository

class SelectMesocycleUseCase(
    private val repository: TrainingRepository
) {
    suspend operator fun invoke(mesocycleId: String) = repository.selectCycle(mesocycleId)
}