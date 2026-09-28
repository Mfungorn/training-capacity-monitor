package com.fungorn.trainingcapacity.core.domain.usecase

import com.fungorn.trainingcapacity.core.domain.model.TrainingProgram
import com.fungorn.trainingcapacity.core.domain.repository.ProgramRepository
import kotlinx.coroutines.flow.Flow

class GetProgramByCodeUseCase(
    private val repository: ProgramRepository
) {
    operator fun invoke(code: String): Flow<TrainingProgram?> = repository.getProgramByCode(code)
}
