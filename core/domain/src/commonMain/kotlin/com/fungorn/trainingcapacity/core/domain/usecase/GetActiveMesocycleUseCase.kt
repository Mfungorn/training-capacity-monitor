package com.fungorn.trainingcapacity.core.domain.usecase

import com.fungorn.trainingcapacity.core.domain.repository.TrainingRepository

class GetActiveMesocycleUseCase(
    private val repository: TrainingRepository
) {
    operator fun invoke() = repository.getSelectedCycle()
}