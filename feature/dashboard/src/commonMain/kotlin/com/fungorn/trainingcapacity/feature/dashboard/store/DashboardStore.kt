package com.fungorn.trainingcapacity.feature.dashboard.store

import com.arkivanov.mvikotlin.core.store.Store
import com.fungorn.trainingcapacity.core.domain.model.TrainingEntry
import com.fungorn.trainingcapacity.feature.dashboard.store.DashboardStore.Intent
import com.fungorn.trainingcapacity.feature.dashboard.store.DashboardStore.Label
import com.fungorn.trainingcapacity.feature.dashboard.store.DashboardStore.State

interface DashboardStore : Store<Intent, State, Label> {
    
    sealed interface Intent {
        data object LoadEntries : Intent
        data class DeleteEntry(val id: String) : Intent
    }
    
    data class State(
        val entries: List<TrainingEntry> = emptyList(),
        val isLoading: Boolean = true,
        val error: String? = null
    )
    
    sealed interface Label {
        data class ShowError(val message: String) : Label
    }
}
