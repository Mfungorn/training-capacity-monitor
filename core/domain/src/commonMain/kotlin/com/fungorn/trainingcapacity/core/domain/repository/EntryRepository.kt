package com.fungorn.trainingcapacity.core.domain.repository

import com.fungorn.trainingcapacity.core.domain.model.TrainingEntry
import kotlinx.coroutines.flow.Flow

interface EntryRepository {
    fun getAllEntries(): Flow<List<TrainingEntry>>
    fun getEntryById(id: String): Flow<TrainingEntry?>
    suspend fun addEntry(entry: TrainingEntry)
}
