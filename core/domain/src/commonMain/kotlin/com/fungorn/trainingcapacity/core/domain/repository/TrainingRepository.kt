package com.fungorn.trainingcapacity.core.domain.repository

import com.fungorn.trainingcapacity.core.domain.model.TrainingEntry
import com.fungorn.trainingcapacity.core.domain.model.TrainingMesocycle
import kotlinx.coroutines.flow.Flow

interface TrainingRepository {
    fun getAllCycles(): Flow<List<TrainingMesocycle>>
    fun getCycleById(id: String): Flow<TrainingMesocycle?>
    fun getSelectedCycle(): Flow<TrainingMesocycle?>
    suspend fun createCycle(cycle: TrainingMesocycle)
    suspend fun selectCycle(mesocycleId: String)

    fun getAllEntries(): Flow<List<TrainingEntry>>
    fun getEntryById(id: String): Flow<TrainingEntry?>
    suspend fun addEntry(entry: TrainingEntry)
}
