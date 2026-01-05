package com.fungorn.trainingcapacity.feature.dashboard.presentation.component

import com.fungorn.trainingcapacity.feature.dashboard.presentation.store.DashboardStore
import kotlinx.coroutines.flow.StateFlow

interface DashboardComponent {
    
    val state: StateFlow<DashboardStore.State>

    fun onRefresh()
    fun onMesocyclesClick()
    fun onProgramsClick()
    fun onAddClick()

    sealed interface Output {
        data object NavigateToMesocycles : Output
        data object NavigateToPrograms : Output
        data object NavigateToForm : Output
    }
}
