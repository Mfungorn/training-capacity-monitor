package com.fungorn.trainingcapacity.feature.programs.presentation.store

import com.arkivanov.mvikotlin.core.store.Store
import com.fungorn.trainingcapacity.core.domain.model.TrainingProgram
import com.fungorn.trainingcapacity.feature.programs.presentation.store.ProgramsStore.Intent
import com.fungorn.trainingcapacity.feature.programs.presentation.store.ProgramsStore.Label
import com.fungorn.trainingcapacity.feature.programs.presentation.store.ProgramsStore.State

interface ProgramsStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data class SelectProgram(val programCode: String) : Intent
    }

    data class State(
        val programs: List<TrainingProgram> = emptyList(),
        val selectedProgramCode: String? = null,
        val isLoading: Boolean = true
    )

    sealed interface Label
}
