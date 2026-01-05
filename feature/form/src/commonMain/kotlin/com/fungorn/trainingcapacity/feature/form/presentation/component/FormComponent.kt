package com.fungorn.trainingcapacity.feature.form.presentation.component

import com.fungorn.trainingcapacity.feature.form.presentation.store.FormStore
import kotlinx.coroutines.flow.StateFlow

interface FormComponent {
    
    val state: StateFlow<FormStore.State>

    fun onMaxCapacitySetsChange(value: Int)
    fun onOverallDifficultyChange(value: Int)
    fun onSaveClick()
    fun onBackClick()
    
    sealed interface Output {
        data object NavigateBack : Output
    }
}
