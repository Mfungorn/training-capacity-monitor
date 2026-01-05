package com.fungorn.trainingcapacity.feature.dashboard.presentation.store

import androidx.compose.runtime.Stable
import com.arkivanov.mvikotlin.core.store.Store
import com.fungorn.trainingcapacity.feature.dashboard.presentation.graph.DashboardDifficultyGraphData
import com.fungorn.trainingcapacity.feature.dashboard.presentation.store.DashboardStore.Intent
import com.fungorn.trainingcapacity.feature.dashboard.presentation.store.DashboardStore.Label
import com.fungorn.trainingcapacity.feature.dashboard.presentation.store.DashboardStore.State

interface DashboardStore : Store<Intent, State, Label> {
    
    sealed interface Intent {
        data object LoadEntries : Intent
    }

    @Stable
    data class State(
        val currentMesocycleName: String = "",
        val currentMesocycleWeekNumber: Int = 0,
        val currentMesocycleWeekCapacityRange: String = "-",
        val isFirstWeek: Boolean = false,
        val isLastWeek: Boolean = false,
        val totalMesocycleWeeksCount: Int = 0,
        val totalMesocycleMaximumCapacitySets: Int = 0,
        val currentProgramName: String = "",
        val currentTrainingDayNumber: Int = 0,
        val totalTrainingDaysCount: Int = 0,
        val spentMaximumCapacitySetsThisWeek: Int = 0,
        val totalMaximumCapacitySetsThisWeek: Int = 0,
        val graphData: DashboardDifficultyGraphData = DashboardDifficultyGraphData(),
        val isLoading: Boolean = true,
        val error: String? = null
    )
    
    sealed interface Label {
        data class ShowError(val message: String) : Label
    }
}
