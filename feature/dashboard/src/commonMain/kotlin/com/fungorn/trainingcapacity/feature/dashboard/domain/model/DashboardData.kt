package com.fungorn.trainingcapacity.feature.dashboard.domain.model

import com.fungorn.trainingcapacity.core.domain.model.SessionStatistics
import com.fungorn.trainingcapacity.core.domain.model.TrainingGroup

data class DashboardData(
    val mesocycleId: String,
    val mesocycleName: String,
    val currentWeekNumber: Int,
    val currentWeekCapacityRange: String,
    val isFirstWeek: Boolean,
    val isLastWeek: Boolean,
    val totalWeeksCount: Int,
    val spentMesocycleMaximumCapacitySets: Int,
    val totalMesocycleMaximumCapacitySets: Int,
    val currentProgramName: String,
    val currentTrainingDayNumber: Int,
    val totalTrainingDaysCount: Int,
    val spentMaximumCapacitySetsThisWeek: Int,
    val totalMaximumCapacitySetsThisWeek: Int,
    val difficultyGraphEntries: List<DifficultyGraphEntry>,
    val mesocycleSessions: SessionStatistics,
    val currentWeekSessions: SessionStatistics,
    val focusGroups: Set<TrainingGroup>,
)

data class DifficultyGraphEntry(
    val formattedDate: String,
    val difficulty: Int
)
