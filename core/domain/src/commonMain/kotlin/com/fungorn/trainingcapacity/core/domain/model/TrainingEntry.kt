package com.fungorn.trainingcapacity.core.domain.model

data class TrainingEntry(
    val id: String,
    val mesocycleId: String,
    val programId: String,
    val weekNumber: Int,
    val trainingDayNumber: Int,
    val spentMaximumCapacitySets: Int,
    val overallDifficulty: Int,
    val createdAt: Long,
    val isCompleted: Boolean = true,
    val durationMinutes: Int = 0,
    val readiness: Int? = null,
    val fatigue: Int? = null,
    val muscleGroupFatigue: Map<TrainingGroup, Int> = emptyMap(),
) {
    companion object {
        const val MIN_FEELING = 1
        const val MAX_FEELING = 5
    }
}
