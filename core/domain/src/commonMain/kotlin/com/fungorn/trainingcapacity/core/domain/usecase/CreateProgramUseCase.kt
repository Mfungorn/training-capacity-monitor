package com.fungorn.trainingcapacity.core.domain.usecase

import com.fungorn.trainingcapacity.core.domain.model.TrainingProgram
import com.fungorn.trainingcapacity.core.domain.repository.ProgramRepository

class CreateProgramUseCase(
    private val repository: ProgramRepository
) {
    suspend operator fun invoke(program: TrainingProgram) = repository.createProgram(program)
}
