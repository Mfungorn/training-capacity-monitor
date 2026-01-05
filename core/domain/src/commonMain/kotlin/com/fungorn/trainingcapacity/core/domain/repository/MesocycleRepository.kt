package com.fungorn.trainingcapacity.core.domain.repository

import com.fungorn.trainingcapacity.core.domain.model.TrainingMesocycle
import kotlinx.coroutines.flow.Flow

interface MesocycleRepository {
    fun getAllCycles(): Flow<List<TrainingMesocycle>>
    fun getCycleById(id: String): Flow<TrainingMesocycle?>
    fun getSelectedCycle(): Flow<TrainingMesocycle?>
    suspend fun createCycle(cycle: TrainingMesocycle)
    suspend fun selectCycle(mesocycleId: String)
}
