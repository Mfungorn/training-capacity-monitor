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
)

@Serializable
data class TrainingEntriesDto(
    val entries: List<TrainingEntryDto> = emptyList()
)
