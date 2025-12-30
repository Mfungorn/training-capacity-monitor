package com.fungorn.trainingcapacity.core.domain.usecase

import com.fungorn.trainingcapacity.core.domain.model.TrainingEntry
import com.fungorn.trainingcapacity.core.domain.repository.TrainingRepository

class AddEntryUseCase(
    private val repository: TrainingRepository
) {
    suspend operator fun invoke(entry: TrainingEntry) = repository.addEntry(entry)
}
