package com.fungorn.trainingcapacity.feature.programs.presentation.store

import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateSet
import com.arkivanov.mvikotlin.core.store.Store
import com.fungorn.trainingcapacity.core.domain.model.TrainingGroup
import com.fungorn.trainingcapacity.core.domain.model.TrainingProgram
import com.fungorn.trainingcapacity.feature.programs.presentation.store.ProgramDetailStore.Intent
import com.fungorn.trainingcapacity.feature.programs.presentation.store.ProgramDetailStore.Label
import com.fungorn.trainingcapacity.feature.programs.presentation.store.ProgramDetailStore.State

interface ProgramDetailStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data class UpdateProgramName(val name: String) : Intent
        data object AddTrainingDay : Intent
        data class RemoveTrainingDay(val index: Int) : Intent
        data class ToggleMuscleGroup(val dayIndex: Int, val group: TrainingGroup) : Intent
        data class ToggleDayExpanded(val dayIndex: Int) : Intent
        data object Save : Intent
    }

    @Stable
    data class State(
        val isCreateMode: Boolean = true,
        val programName: String = "",
        val trainingDays: SnapshotStateList<TrainingDayState> =
            SnapshotStateList<TrainingDayState>().apply {
                add(
                    TrainingDayState(
                        name = "Day #1",
                        muscleGroups = SnapshotStateSet(),
                        isExpanded = true
                    )
                )
            },
        val isSaving: Boolean = false,
        val error: String? = null
    )

    @Stable
    data class TrainingDayState(
        val name: String,
        val muscleGroups: SnapshotStateSet<TrainingGroup>,
        val isExpanded: Boolean = false
    )

    sealed interface Label {
        data object NavigateBack : Label
    }
}

fun TrainingProgram.toDetailState(): State = State(
    isCreateMode = false,
    programName = this.name,
    trainingDays = this.trainingDays.let {
        SnapshotStateList(it.size) { index ->
            val day = it[index]
            ProgramDetailStore.TrainingDayState(
                name = day.name,
                muscleGroups = SnapshotStateSet<TrainingGroup>().apply {
                    addAll(day.muscleGroups)
                },
                isExpanded = false
            )
        }
    }
)
