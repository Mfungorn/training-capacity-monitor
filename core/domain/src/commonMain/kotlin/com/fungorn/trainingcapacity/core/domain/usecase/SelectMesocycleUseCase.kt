package com.fungorn.trainingcapacity.core.domain.usecase

import com.fungorn.trainingcapacity.core.domain.repository.MesocycleRepository

class SelectMesocycleUseCase(
    private val repository: MesocycleRepository
) {
    suspend operator fun invoke(mesocycleId: String) = repository.selectCycle(mesocycleId)
}