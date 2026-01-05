package com.fungorn.trainingcapacity.feature.programs.presentation.component

import com.fungorn.trainingcapacity.core.domain.model.TrainingProgram
import com.fungorn.trainingcapacity.feature.programs.presentation.store.ProgramsStore
import kotlinx.coroutines.flow.StateFlow

interface ProgramsComponent {

    val state: StateFlow<ProgramsStore.State>

    fun onProgramClick(program: TrainingProgram)
    fun onBackClick()

    sealed interface Output {
        data object NavigateBack : Output
    }
}
