package com.fungorn.trainingcapacity.core.domain.usecase

import com.fungorn.trainingcapacity.core.domain.repository.ProgramRepository

class SelectProgramUseCase(
    private val repository: ProgramRepository
) {
    suspend operator fun invoke(programCode: String) = repository.selectProgram(programCode)
}
