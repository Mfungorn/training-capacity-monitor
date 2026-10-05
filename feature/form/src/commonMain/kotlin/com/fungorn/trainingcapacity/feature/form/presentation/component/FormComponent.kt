package com.fungorn.trainingcapacity.feature.form.presentation.component

import com.fungorn.trainingcapacity.core.domain.model.TrainingGroup
import com.fungorn.trainingcapacity.feature.form.presentation.store.FormStore
import kotlinx.coroutines.flow.StateFlow

interface FormComponent {
    
    val state: StateFlow<FormStore.State>

    fun onProgramSelect(code: String)
    fun onTrainingDaySelect(number: Int)
    fun onCompletedChange(isCompleted: Boolean)
    fun onMaxCapacitySetsChange(value: Int)
    fun onOverallDifficultyChange(value: Int)
    fun onDurationChange(minutes: Int)
    fun onReadinessChange(value: Int)
    fun onFatigueChange(value: Int)
    fun onMuscleGroupFatigueChange(group: TrainingGroup, value: Int)
    fun onSaveClick()
    fun onBackClick()
    
    sealed interface Output {
        data object NavigateBack : Output
    }
}
