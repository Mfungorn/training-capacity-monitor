package com.fungorn.trainingcapacity.feature.programs.presentation.store

import com.arkivanov.mvikotlin.core.store.Store
import com.fungorn.trainingcapacity.core.domain.model.TrainingProgram
import com.fungorn.trainingcapacity.feature.programs.presentation.store.ProgramsStore.Intent
import com.fungorn.trainingcapacity.feature.programs.presentation.store.ProgramsStore.Label
import com.fungorn.trainingcapacity.feature.programs.presentation.store.ProgramsStore.State

interface ProgramsStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data class SelectProgram(val program: TrainingProgram) : Intent
    }

    data class State(
        val programs: List<TrainingProgram> = listOf(TrainingProgram.UpperLower),
        val selectedProgram: TrainingProgram = TrainingProgram.UpperLower,
        val isLoading: Boolean = false
    )

    sealed interface Label {
        // For future use when creating new programs is available
    }
}
