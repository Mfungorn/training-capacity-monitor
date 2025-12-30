package com.fungorn.trainingcapacity.feature.form.component

import com.fungorn.trainingcapacity.feature.form.store.FormStore
import kotlinx.coroutines.flow.StateFlow

interface FormComponent {
    
    val state: StateFlow<FormStore.State>

    // TODO

    fun onSaveClick()
    fun onBackClick()
    
    sealed interface Output {
        data object NavigateBack : Output
    }
}
