package com.fungorn.trainingcapacity.feature.form.presentation.store

import com.arkivanov.mvikotlin.core.store.Store
import com.fungorn.trainingcapacity.core.domain.model.TrainingDay
import com.fungorn.trainingcapacity.core.domain.model.TrainingGroup
import com.fungorn.trainingcapacity.core.domain.model.TrainingProgram
import com.fungorn.trainingcapacity.feature.form.presentation.store.FormStore.Intent
import com.fungorn.trainingcapacity.feature.form.presentation.store.FormStore.Label
import com.fungorn.trainingcapacity.feature.form.presentation.store.FormStore.State

interface FormStore : Store<Intent, State, Label> {
    
    sealed interface Intent {
        data class LoadEntry(val id: String) : Intent
        data class SelectProgram(val code: String) : Intent
        data class SelectTrainingDay(val number: Int) : Intent
        data class UpdateCompleted(val isCompleted: Boolean) : Intent
        data class UpdateMaxCapacitySets(val value: Int) : Intent
        data class UpdateOverallDifficulty(val value: Int) : Intent
        data class UpdateDuration(val minutes: Int) : Intent
        data class UpdateReadiness(val value: Int) : Intent
        data class UpdateFatigue(val value: Int) : Intent
        data class UpdateMuscleGroupFatigue(val group: TrainingGroup, val value: Int) : Intent
        data object SaveEntry : Intent
    }
    
    data class State(
        val id: String? = null,
        val weekNumber: Int? = null,
        val programs: List<TrainingProgram> = emptyList(),
        val selectedProgramCode: String? = null,
        val trainingDayNumber: Int = 1,
        val isCompleted: Boolean = true,
        val maxCapacitySets: Int = 4,
        val overallDifficulty: Int = 2,
        val durationMinutes: Int = 60,
        val readiness: Int? = null,
        val fatigue: Int? = null,
        val muscleGroupFatigue: Map<TrainingGroup, Int> = emptyMap(),
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
    ) {
        val selectedProgram: TrainingProgram? get() = programs.find { it.code == selectedProgramCode }
        val selectedTrainingDay: TrainingDay?
            get() = selectedProgram?.trainingDays?.getOrNull(
                trainingDayNumber - 1
            )
        val trainingDayGroups: List<TrainingGroup>
            get() = selectedTrainingDay?.muscleGroups?.sortedBy(TrainingGroup::ordinal).orEmpty()
        val sessionTitle: String? get() = weekNumber?.let { "Week ${it + 1} · Day $trainingDayNumber" }
        val canSave: Boolean
            get() = !isLoading && selectedProgram != null &&
                    (!isCompleted || (readiness != null && fatigue != null))
    }
    
    sealed interface Label {
        data object EntrySaved : Label
        data class ShowError(val message: String) : Label
    }
}
