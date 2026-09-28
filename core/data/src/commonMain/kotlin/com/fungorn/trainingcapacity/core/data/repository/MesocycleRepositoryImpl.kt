package com.fungorn.trainingcapacity.core.data.repository

import com.fungorn.trainingcapacity.core.data.local.TrainingMesocyclesLocalDataSource
import com.fungorn.trainingcapacity.core.data.local.TrainingProgramsLocalDataSource
import com.fungorn.trainingcapacity.core.data.mapper.toDomain
import com.fungorn.trainingcapacity.core.data.mapper.toDto
import com.fungorn.trainingcapacity.core.domain.model.TrainingMesocycle
import com.fungorn.trainingcapacity.core.domain.repository.MesocycleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class MesocycleRepositoryImpl(
    private val localDataSource: TrainingMesocyclesLocalDataSource,
    private val programsDataSource: TrainingProgramsLocalDataSource
) : MesocycleRepository {

    companion object {
        private const val FALLBACK_PROGRAM_CODE = "DL0"
    }

    override fun getAllCycles(): Flow<List<TrainingMesocycle>> =
        localDataSource.getAllCycles().combine(programsDataSource.getAllPrograms()) { cycles, programs ->
            val fallbackProgram = programs.find { it.code == FALLBACK_PROGRAM_CODE }?.toDomain()
            cycles.mapNotNull { cycleDto ->
                val program = programs.find { it.code == cycleDto.programCode }?.toDomain()
                    ?: fallbackProgram
                program?.let { cycleDto.toDomain(it) }
            }
        }

    override fun getCycleById(id: String): Flow<TrainingMesocycle?> =
        localDataSource.getCycleById(id).combine(programsDataSource.getAllPrograms()) { cycleDto, programs ->
            cycleDto?.let { dto ->
                val fallbackProgram = programs.find { it.code == FALLBACK_PROGRAM_CODE }?.toDomain()
                val program = programs.find { it.code == dto.programCode }?.toDomain()
                    ?: fallbackProgram
                program?.let { dto.toDomain(it) }
            }
        }

    override fun getSelectedCycle(): Flow<TrainingMesocycle?> =
        localDataSource.getSelectedCycle().combine(programsDataSource.getAllPrograms()) { cycleDto, programs ->
            cycleDto?.let { dto ->
                val fallbackProgram = programs.find { it.code == FALLBACK_PROGRAM_CODE }?.toDomain()
                val program = programs.find { it.code == dto.programCode }?.toDomain()
                    ?: fallbackProgram
                program?.let { dto.toDomain(it) }
            }
        }

    override suspend fun createCycle(cycle: TrainingMesocycle) {
        localDataSource.createCycle(cycle.toDto())
    }

    override suspend fun selectCycle(mesocycleId: String) {
        localDataSource.selectCycle(mesocycleId)
    }
}
