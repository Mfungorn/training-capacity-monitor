package com.fungorn.trainingcapacity.core.data.repository

import com.fungorn.trainingcapacity.core.data.local.LocalDataSource
import com.fungorn.trainingcapacity.core.data.mapper.toDomain
import com.fungorn.trainingcapacity.core.data.mapper.toDto
import com.fungorn.trainingcapacity.core.domain.model.TrainingEntry
import com.fungorn.trainingcapacity.core.domain.repository.TrainingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrainingRepositoryImpl(
    private val localDataSource: LocalDataSource
) : TrainingRepository {
    
    override fun getAllEntries(): Flow<List<TrainingEntry>> =
        localDataSource.getAllEntries().map { entries ->
            entries.map { it.toDomain() }
        }
    
    override fun getEntryById(id: String): Flow<TrainingEntry?> =
        localDataSource.getEntryById(id).map { it?.toDomain() }
    
    override suspend fun addEntry(entry: TrainingEntry) {
        localDataSource.saveEntry(entry.toDto())
    }
    
    override suspend fun updateEntry(entry: TrainingEntry) {
        localDataSource.saveEntry(entry.toDto())
    }
    
    override suspend fun deleteEntry(id: String) {
        localDataSource.deleteEntry(id)
    }
}
