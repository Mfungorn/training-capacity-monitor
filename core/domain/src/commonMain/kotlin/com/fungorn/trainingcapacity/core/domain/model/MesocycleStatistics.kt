package com.fungorn.trainingcapacity.core.domain.model

data class MesocycleStatistics(
    val mesocycle: TrainingMesocycle,
    val weeks: List<WeekStatistics>,
    val sessions: SessionStatistics,
    val entries: List<TrainingEntry>,
)

data class WeekStatistics(
    val weekNumber: Int,
    val week: TrainingMesocycle.Week,
    val sessions: SessionStatistics,
)

data class SessionStatistics(
    val plannedSessions: Int,
    val completedSessions: Int,
    val skippedSessions: Int,
    val totalDurationMinutes: Int,
    val spentMaximumCapacitySets: Int,
    val averageDifficulty: Float?,
    val averageReadiness: Float?,
    val averageFatigue: Float?,
    val muscleGroupFatigue: Map<TrainingGroup, Float>,
) {
    val loggedSessions: Int get() = completedSessions + skippedSessions

    /** Every planned session has been logged (completed or skipped). */
    val isComplete: Boolean get() = plannedSessions > 0 && loggedSessions >= plannedSessions

    /** Completed sessions relative to everything planned for the period. */
    val adherence: Float? get() = ratio(completedSessions, plannedSessions)

    /** Completed sessions relative to the sessions that have already been logged (completed or skipped). */
    val adherenceToDate: Float? get() = ratio(completedSessions, loggedSessions)

    val averageDurationMinutes: Float? get() = ratio(totalDurationMinutes, completedSessions)

    private fun ratio(numerator: Int, denominator: Int): Float? =
        if (denominator == 0) null else numerator.toFloat() / denominator

    companion object {
        val EMPTY = SessionStatistics(
            plannedSessions = 0,
            completedSessions = 0,
            skippedSessions = 0,
            totalDurationMinutes = 0,
            spentMaximumCapacitySets = 0,
            averageDifficulty = null,
            averageReadiness = null,
            averageFatigue = null,
            muscleGroupFatigue = emptyMap(),
        )
    }
}
