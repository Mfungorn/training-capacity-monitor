package com.fungorn.trainingcapacity.feature.mesocycles.presentation.component

import com.fungorn.trainingcapacity.feature.mesocycles.presentation.store.MesocyclesStore
import kotlinx.coroutines.flow.StateFlow

interface MesocyclesComponent {

    val state: StateFlow<MesocyclesStore.State>

    fun onMesocycleClick(id: String)
    fun onStartNewMesocycleClick()
    fun onBackClick()

    sealed interface Output {
        data object NavigateBack : Output
        data object NavigateToCreateMesocycle : Output
        data class NavigateToViewMesocycle(val id: String) : Output
    }
}
