package com.fungorn.trainingcapacity.core.data.local.dto

import kotlinx.serialization.Serializable

@Serializable
data class TrainingProgramDto(
    val code: String,
    val name: String,
    val trainingDays: List<TrainingDayDto> = emptyList(),
    val isBuiltIn: Boolean = false
)

@Serializable
data class TrainingDayDto(
    val id: String,
    val name: String,
    val muscleGroups: List<String> = emptyList()
)

@Serializable
data class TrainingProgramsDto(
    val programs: List<TrainingProgramDto> = emptyList()
)
