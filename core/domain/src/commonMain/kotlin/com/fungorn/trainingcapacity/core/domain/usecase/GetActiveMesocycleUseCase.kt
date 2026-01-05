package com.fungorn.trainingcapacity.core.domain.usecase

import com.fungorn.trainingcapacity.core.domain.repository.MesocycleRepository

class GetActiveMesocycleUseCase(
    private val repository: MesocycleRepository
) {
    operator fun invoke() = repository.getSelectedCycle()
}