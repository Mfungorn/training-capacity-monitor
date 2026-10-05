package com.fungorn.trainingcapacity.core.data.local.dto

import kotlinx.serialization.Serializable

@Serializable
data class TrainingEntryDto(
    val id: String,
    val mesocycleId: String,
    val code: String,
    val spentMaximumCapacitySets: Int,
    val overallDifficulty: Int,
    val createdAt: Long,
    val isCompleted: Boolean = true,
    val durationMinutes: Int = 0,
    val readiness: Int? = null,
    val fatigue: Int? = null,
    val muscleGroupFatigue: Map<String, Int> = emptyMap(),
)

@Serializable
data class TrainingEntriesDto(
    val entries: List<TrainingEntryDto> = emptyList()
)
