package com.fungorn.trainingcapacity.core.data.repository

import com.fungorn.trainingcapacity.core.data.local.TrainingMesocyclesLocalDataSource
import com.fungorn.trainingcapacity.core.data.local.TrainingProgramsLocalDataSource
import com.fungorn.trainingcapacity.core.data.mapper.toDomain
import com.fungorn.trainingcapacity.core.data.mapper.toDto
import com.fungorn.trainingcapacity.core.domain.model.TrainingProgram
import com.fungorn.trainingcapacity.core.domain.repository.ProgramRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ProgramRepositoryImpl(
    private val localDataSource: TrainingProgramsLocalDataSource,
    private val mesocyclesDataSource: TrainingMesocyclesLocalDataSource
) : ProgramRepository {

    override fun getAllPrograms(): Flow<List<TrainingProgram>> =
        localDataSource.getAllPrograms().map { programs ->
            programs.map { it.toDomain() }
        }

    override fun getProgramByCode(code: String): Flow<TrainingProgram?> =
        localDataSource.getProgramByCode(code).map { it?.toDomain() }

    override fun getSelectedProgram(): Flow<TrainingProgram?> =
        localDataSource.getSelectedProgram().map { it?.toDomain() }

    override suspend fun selectProgram(code: String) {
        localDataSource.selectProgram(code)
    }

    override suspend fun createProgram(program: TrainingProgram) {
        localDataSource.createProgram(program.toDto())
    }

    override suspend fun deleteProgram(code: String) {
        val isUsed = mesocyclesDataSource.isProgramUsedByAnyCycle(code).first()
        if (isUsed) {
            throw IllegalStateException("Cannot delete program that is used by a mesocycle")
        }
        localDataSource.deleteProgram(code)
    }
}
