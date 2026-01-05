package com.fungorn.trainingcapacity.feature.mesocycles.presentation.store

import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.arkivanov.mvikotlin.core.store.Store
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.model.MesocycleListItem
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.store.MesocyclesStore.Intent
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.store.MesocyclesStore.Label
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.store.MesocyclesStore.State

interface MesocyclesStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data class OpenMesocycle(val id: String) : Intent
        data object StartNewMesocycle : Intent
    }

    @Stable
    data class State(
        val mesocycles: SnapshotStateList<MesocycleListItem> = SnapshotStateList(),
        val currentMesocycle: MesocycleListItem? = null,
        val isLoading: Boolean = true,
        val errorMessage: String? = null
    )

    sealed interface Label {
        data object NavigateToCreateMesocycle : Label
        data class NavigateToViewMesocycle(val id: String) : Label
    }
}
