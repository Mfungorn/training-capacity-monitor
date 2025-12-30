package com.fungorn.trainingcapacity.core.domain.usecase

import com.fungorn.trainingcapacity.core.domain.repository.TrainingRepository

class DeleteEntryUseCase(
    private val repository: TrainingRepository
) {
    suspend operator fun invoke(id: String) = repository.deleteEntry(id)
}
