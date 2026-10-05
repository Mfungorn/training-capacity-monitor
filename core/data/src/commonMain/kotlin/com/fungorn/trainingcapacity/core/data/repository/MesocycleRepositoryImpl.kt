package com.fungorn.trainingcapacity.core.data.repository

import com.fungorn.trainingcapacity.core.data.local.TrainingMesocyclesLocalDataSource
import com.fungorn.trainingcapacity.core.data.local.TrainingProgramsLocalDataSource
import com.fungorn.trainingcapacity.core.data.local.dto.TrainingMesocycleDto
import com.fungorn.trainingcapacity.core.data.local.dto.TrainingProgramDto
import com.fungorn.trainingcapacity.core.data.mapper.toDomain
import com.fungorn.trainingcapacity.core.data.mapper.toDto
import com.fungorn.trainingcapacity.core.domain.model.TrainingMesocycle
import com.fungorn.trainingcapacity.core.domain.model.TrainingProgram
import com.fungorn.trainingcapacity.core.domain.repository.MesocycleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class MesocycleRepositoryImpl(
    private val localDataSource: TrainingMesocyclesLocalDataSource,
    private val programsDataSource: TrainingProgramsLocalDataSource
) : MesocycleRepository {

    override fun getAllCycles(): Flow<List<TrainingMesocycle>> =
        localDataSource.getAllCycles().combine(programsDataSource.getAllPrograms()) { cycles, programs ->
            cycles.map { it.toDomain(programs) }
        }

    override fun getCycleById(id: String): Flow<TrainingMesocycle?> =
        localDataSource.getCycleById(id).withPrograms()

    override fun getSelectedCycle(): Flow<TrainingMesocycle?> =
        localDataSource.getSelectedCycle().withPrograms()

    override suspend fun createCycle(cycle: TrainingMesocycle) {
        localDataSource.createCycle(cycle.toDto())
    }

    override suspend fun selectCycle(mesocycleId: String) {
        localDataSource.selectCycle(mesocycleId)
    }

    private fun Flow<TrainingMesocycleDto?>.withPrograms(): Flow<TrainingMesocycle?> =
        combine(programsDataSource.getAllPrograms()) { cycleDto, programs ->
            cycleDto?.toDomain(programs)
        }

    /**
     * Resolves the mesocycle's program; if it was deleted (or was a built-in that no longer exists),
     * a placeholder program keeps the mesocycle and its history readable.
     */
    private fun TrainingMesocycleDto.toDomain(programs: List<TrainingProgramDto>): TrainingMesocycle =
        toDomain(
            programs.find { it.code == programCode }?.toDomain() ?: missingProgram(programCode)
        )

    private fun missingProgram(code: String) = TrainingProgram(
        code = code,
        name = "Unknown program",
        trainingDays = emptyList()
    )
}
