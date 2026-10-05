package com.fungorn.trainingcapacity.feature.mesocycles.presentation.store

import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.arkivanov.mvikotlin.core.store.Store
import com.fungorn.trainingcapacity.core.domain.model.GroupPriority
import com.fungorn.trainingcapacity.core.domain.model.MesocycleStatistics
import com.fungorn.trainingcapacity.core.domain.model.TrainingGroup
import com.fungorn.trainingcapacity.core.domain.model.TrainingMesocycle
import com.fungorn.trainingcapacity.core.domain.model.TrainingProgram
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
        data class SelectProgram(val code: String) : Intent
        data class SetGroupPriority(val group: TrainingGroup, val priority: GroupPriority?) : Intent
        data object Save : Intent
        data object Export : Intent
    }

    @Stable
    data class State(
        val mode: Mode = Mode.CREATE,
        val startDateMillis: Long = Clock.System.now().toEpochMilliseconds(),
        val weeks: SnapshotStateList<WeekState> = SnapshotStateList(),
        val programs: List<TrainingProgram> = emptyList(),
        val selectedProgramCode: String? = null,
        val groupPriorities: Map<TrainingGroup, GroupPriority> = emptyMap(),
        val statistics: MesocycleStatistics? = null,
        val isLoading: Boolean = false,
        val isExporting: Boolean = false,
        val errorMessage: String? = null
    ) {
        val isCreateMode: Boolean get() = mode == Mode.CREATE
        val selectedProgram: TrainingProgram? get() = programs.find { it.code == selectedProgramCode }
        val canStart: Boolean get() = !isLoading && weeks.isNotEmpty() && selectedProgram != null
        val focusGroupsCount: Int get() = groupPriorities.count { it.value == GroupPriority.FOCUS }
        val canAddFocusGroup: Boolean get() = focusGroupsCount < TrainingMesocycle.MAX_FOCUS_GROUPS
    }

    sealed interface Label {
        data object MesocycleSaved : Label
        data class ShowError(val message: String) : Label
    }
}
