package com.fungorn.trainingcapacity.core.domain.usecase

import com.fungorn.trainingcapacity.core.domain.model.TrainingEntry
import com.fungorn.trainingcapacity.core.domain.repository.EntryRepository
import kotlinx.coroutines.flow.Flow

class GetEntryByIdUseCase(
    private val repository: EntryRepository
) {
    operator fun invoke(id: String): Flow<TrainingEntry?> = repository.getEntryById(id)
}
