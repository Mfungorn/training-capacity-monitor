package com.fungorn.trainingcapacity.feature.dashboard.component

import com.fungorn.trainingcapacity.feature.dashboard.store.DashboardStore
import kotlinx.coroutines.flow.StateFlow

interface DashboardComponent {
    
    val state: StateFlow<DashboardStore.State>
    
    fun onMesocycleClick()
    fun onProgramClick()
    fun onAddClick()

    sealed interface Output {
        data object NavigateToMesocycles : Output
        data object NavigateToPrograms : Output
        data object NavigateToForm : Output
    }
}
