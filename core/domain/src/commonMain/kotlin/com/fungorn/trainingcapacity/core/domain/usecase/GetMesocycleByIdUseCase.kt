package com.fungorn.trainingcapacity.core.domain.usecase

import com.fungorn.trainingcapacity.core.domain.model.TrainingMesocycle
import com.fungorn.trainingcapacity.core.domain.repository.MesocycleRepository
import kotlinx.coroutines.flow.Flow

class GetMesocycleByIdUseCase(
    private val mesocycleRepository: MesocycleRepository
) {
    operator fun invoke(id: String): Flow<TrainingMesocycle?> =
        mesocycleRepository.getCycleById(id)
}
