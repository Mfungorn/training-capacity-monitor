package com.fungorn.trainingcapacity.core.data.mapper

import com.fungorn.trainingcapacity.core.data.local.dto.TrainingEntryDto
import com.fungorn.trainingcapacity.core.domain.model.TrainingEntry

fun TrainingEntryDto.toDomain(): TrainingEntry = TrainingEntry(
    id = id,
    mesocycleId = mesocycleId,
    programId = programId,
    trainingDayNumber = trainingDayNumber,
    weekNumber = weekNumber,
    spentMaximumCapacitySets = spentMaximumCapacitySets,
    overallDifficulty = overallDifficulty,
    createdAt = createdAt,
)

fun TrainingEntry.toDto(): TrainingEntryDto = TrainingEntryDto(
    id = id,
    mesocycleId = mesocycleId,
    code = buildTrainingCode(),
    spentMaximumCapacitySets = spentMaximumCapacitySets,
    overallDifficulty = overallDifficulty,
    createdAt = createdAt,
)

private fun TrainingEntry.buildTrainingCode(): String = programId +
        "_" + trainingDayNumber +
        "_" + weekNumber

private val TrainingEntryDto.programId: String
    get() = code.split("_").firstOrNull() ?: ""

private val TrainingEntryDto.trainingDayNumber: Int
    get() = code.split("_").drop(1).firstOrNull()?.toIntOrNull() ?: 0

private val TrainingEntryDto.weekNumber: Int
    get() = code.substringAfterLast("_").toIntOrNull() ?: 0
