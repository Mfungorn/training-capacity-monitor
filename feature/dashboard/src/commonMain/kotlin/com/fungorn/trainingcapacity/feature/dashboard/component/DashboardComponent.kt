package com.fungorn.trainingcapacity.feature.dashboard.component

import com.fungorn.trainingcapacity.feature.dashboard.store.DashboardStore
import kotlinx.coroutines.flow.StateFlow

interface DashboardComponent {
    
    val state: StateFlow<DashboardStore.State>
    
    fun onAddClick()
    fun onEntryClick(id: String)
    fun onDeleteClick(id: String)
    
    sealed interface Output {
        data object NavigateToForm : Output
        data class NavigateToEdit(val id: String) : Output
    }
}
