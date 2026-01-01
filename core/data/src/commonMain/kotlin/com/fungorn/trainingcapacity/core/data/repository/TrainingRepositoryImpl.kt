package com.fungorn.trainingcapacity.core.data.repository

import com.fungorn.trainingcapacity.core.data.local.TrainingEntriesLocalDataSource
import com.fungorn.trainingcapacity.core.data.local.TrainingMesocyclesLocalDataSource
import com.fungorn.trainingcapacity.core.data.mapper.toDomain
import com.fungorn.trainingcapacity.core.data.mapper.toDto
import com.fungorn.trainingcapacity.core.domain.model.TrainingEntry
import com.fungorn.trainingcapacity.core.domain.model.TrainingMesocycle
import com.fungorn.trainingcapacity.core.domain.repository.TrainingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrainingRepositoryImpl(
    private val trainingCyclesLocalDataSource: TrainingMesocyclesLocalDataSource,
    private val trainingEntriesLocalDataSource: TrainingEntriesLocalDataSource
) : TrainingRepository {

    override fun getAllCycles(): Flow<List<TrainingMesocycle>> =
        trainingCyclesLocalDataSource.getAllCycles().map { cycles ->
            cycles.map { it.toDomain() }
        }

    override fun getCycleById(id: String): Flow<TrainingMesocycle?> =
        trainingCyclesLocalDataSource.getCycleById(id).map { it?.toDomain() }

    override suspend fun createCycle(cycle: TrainingMesocycle) {
        trainingCyclesLocalDataSource.createCycle(cycle.toDto())
    }

    override suspend fun selectCycle(mesocycleId: String) {
        trainingCyclesLocalDataSource.selectCycle(mesocycleId)
    }

    override fun getSelectedCycle(): Flow<TrainingMesocycle?> =
        trainingCyclesLocalDataSource.getSelectedCycle().map { it?.toDomain() }

    override fun getAllEntries(): Flow<List<TrainingEntry>> =
        trainingEntriesLocalDataSource.getAllEntries().map { entries ->
            entries.map { it.toDomain() }
        }

    override fun getEntryById(id: String): Flow<TrainingEntry?> =
        trainingEntriesLocalDataSource.getEntryById(id).map { it?.toDomain() }

    override suspend fun addEntry(entry: TrainingEntry) {
        trainingEntriesLocalDataSource.saveEntry(entry.toDto())
    }
}
