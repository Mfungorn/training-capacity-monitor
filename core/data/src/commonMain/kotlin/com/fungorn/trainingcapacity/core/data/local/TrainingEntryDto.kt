package com.fungorn.trainingcapacity.core.data.local

import kotlinx.serialization.Serializable

@Serializable
data class TrainingEntryDto(
    val id: String,
    val code: String,
    val weekNumber: Int,
    val maximumCapacitySets: Int,
    val overallDifficulty: Int,
    val createdAt: Long,
    val updatedAt: Long
)

@Serializable
data class TrainingEntriesDto(
    val entries: List<TrainingEntryDto> = emptyList()
)
