package com.fungorn.trainingcapacity.feature.programs.presentation.component

import com.fungorn.trainingcapacity.core.domain.model.TrainingGroup
import com.fungorn.trainingcapacity.feature.programs.presentation.store.ProgramDetailStore
import kotlinx.coroutines.flow.StateFlow

interface ProgramDetailComponent {

    val state: StateFlow<ProgramDetailStore.State>

    fun onProgramNameChange(name: String)
    fun onAddTrainingDay()
    fun onRemoveTrainingDay(index: Int)
    fun onToggleMuscleGroup(dayIndex: Int, group: TrainingGroup)
    fun onToggleDayExpanded(dayIndex: Int)
    fun onSaveClick()
    fun onCancelClick()

    sealed interface Output {
        data object NavigateBack : Output
    }
}
