package com.fungorn.trainingcapacity.feature.mesocycles.presentation.store

import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.arkivanov.mvikotlin.core.store.Store
import com.fungorn.trainingcapacity.core.domain.model.TrainingMesocycle
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.model.Mode
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.model.RirRange
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.model.WeekState
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.store.MesocycleDetailStore.Intent
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.store.MesocycleDetailStore.Label
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.store.MesocycleDetailStore.State
import kotlin.time.Clock

interface MesocycleDetailStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data class UpdateStartDate(val millis: Long) : Intent
        data class AddWeek(val type: TrainingMesocycle.Week.Type, val rirRange: RirRange) : Intent
        data class UpdateWeek(
            val index: Int,
            val type: TrainingMesocycle.Week.Type,
            val rirRange: RirRange
        ) : Intent

        data class RemoveWeek(val index: Int) : Intent
        data object Save : Intent
    }

    @Stable
    data class State(
        val mode: Mode = Mode.CREATE,
        val startDateMillis: Long = Clock.System.now().toEpochMilliseconds(),
        val weeks: SnapshotStateList<WeekState> = SnapshotStateList(),
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    ) {
        val isCreateMode: Boolean get() = mode == Mode.CREATE
    }

    sealed interface Label {
        data object MesocycleSaved : Label
        data class ShowError(val message: String) : Label
    }
}
