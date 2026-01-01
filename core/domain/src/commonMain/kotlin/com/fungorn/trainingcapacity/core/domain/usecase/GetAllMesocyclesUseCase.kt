package com.fungorn.trainingcapacity.core.domain.usecase

import com.fungorn.trainingcapacity.core.domain.model.TrainingMesocycle
import com.fungorn.trainingcapacity.core.domain.repository.TrainingRepository
import kotlinx.coroutines.flow.Flow

class GetAllMesocyclesUseCase(
    private val repository: TrainingRepository
) {
    operator fun invoke(): Flow<List<TrainingMesocycle>> = repository.getAllCycles()
}
