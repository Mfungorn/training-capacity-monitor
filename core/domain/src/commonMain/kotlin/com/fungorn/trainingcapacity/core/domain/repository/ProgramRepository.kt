package com.fungorn.trainingcapacity.core.domain.repository

import com.fungorn.trainingcapacity.core.domain.model.TrainingProgram
import kotlinx.coroutines.flow.Flow

interface ProgramRepository {
    fun getAllPrograms(): Flow<List<TrainingProgram>>
    fun getProgramByCode(code: String): Flow<TrainingProgram?>
    fun getSelectedProgram(): Flow<TrainingProgram?>
    suspend fun selectProgram(code: String)
    suspend fun createProgram(program: TrainingProgram)
    suspend fun deleteProgram(code: String)
}
