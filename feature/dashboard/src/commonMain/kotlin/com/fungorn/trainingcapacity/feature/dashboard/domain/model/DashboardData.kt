package com.fungorn.trainingcapacity.feature.dashboard.domain.model

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
    val difficultyGraphEntries: List<DifficultyGraphEntry>
)

data class DifficultyGraphEntry(
    val formattedDate: String,
    val difficulty: Int
)
