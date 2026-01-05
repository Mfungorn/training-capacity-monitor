package com.fungorn.trainingcapacity.feature.form.presentation.store

import com.arkivanov.mvikotlin.core.store.Store
import com.fungorn.trainingcapacity.feature.form.presentation.store.FormStore.Intent
import com.fungorn.trainingcapacity.feature.form.presentation.store.FormStore.Label
import com.fungorn.trainingcapacity.feature.form.presentation.store.FormStore.State

interface FormStore : Store<Intent, State, Label> {
    
    sealed interface Intent {
        data class LoadEntry(val id: String) : Intent

        // TODO

        data object SaveEntry : Intent
    }
    
    data class State(
        val id: String? = null,
        // TODO
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
    )
    
    sealed interface Label {
        data object EntrySaved : Label
        data class ShowError(val message: String) : Label
    }
}
