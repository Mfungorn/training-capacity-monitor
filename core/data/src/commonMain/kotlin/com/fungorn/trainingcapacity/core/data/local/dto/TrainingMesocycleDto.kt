package com.fungorn.trainingcapacity.core.data.local.dto

import kotlinx.serialization.Serializable

@Serializable
data class TrainingMesocycleDto(
    val id: String,
    val weeks: List<WeekDto> = emptyList(),
    val isSelected: Boolean = false,
    val startedAt: Long,
) {
    @Serializable
    data class WeekDto(
        val name: String,
        val maximumCapacityLowerEnd: Int,
        val maximumCapacityUpperEnd: Int,
    )
}

@Serializable
data class TrainingMesocyclesDto(
    val cycles: List<TrainingMesocycleDto> = emptyList()
)
