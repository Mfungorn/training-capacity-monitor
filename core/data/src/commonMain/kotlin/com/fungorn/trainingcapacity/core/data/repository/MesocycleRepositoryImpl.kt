package com.fungorn.trainingcapacity.core.data.repository

import com.fungorn.trainingcapacity.core.data.local.TrainingMesocyclesLocalDataSource
import com.fungorn.trainingcapacity.core.data.mapper.toDomain
import com.fungorn.trainingcapacity.core.data.mapper.toDto
import com.fungorn.trainingcapacity.core.domain.model.TrainingMesocycle
import com.fungorn.trainingcapacity.core.domain.repository.MesocycleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MesocycleRepositoryImpl(
    private val localDataSource: TrainingMesocyclesLocalDataSource
) : MesocycleRepository {

    override fun getAllCycles(): Flow<List<TrainingMesocycle>> =
        localDataSource.getAllCycles().map { cycles ->
            cycles.map { it.toDomain() }
        }

    override fun getCycleById(id: String): Flow<TrainingMesocycle?> =
        localDataSource.getCycleById(id).map { it?.toDomain() }

    override fun getSelectedCycle(): Flow<TrainingMesocycle?> =
        localDataSource.getSelectedCycle().map { it?.toDomain() }

    override suspend fun createCycle(cycle: TrainingMesocycle) {
        localDataSource.createCycle(cycle.toDto())
    }

    override suspend fun selectCycle(mesocycleId: String) {
        localDataSource.selectCycle(mesocycleId)
    }
}
