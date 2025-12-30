package com.fungorn.trainingcapacity.core.data.mapper

import com.fungorn.trainingcapacity.core.data.local.TrainingEntryDto
import com.fungorn.trainingcapacity.core.domain.model.TrainingEntry
import com.fungorn.trainingcapacity.core.domain.model.TrainingProgram

fun TrainingEntryDto.toDomain(): TrainingEntry = TrainingEntry(
    id = id,
    program = trainingProgram,
    trainingDayNumber = trainingDayNumber,
    weekNumber = weekNumber,
    maximumCapacitySets = maximumCapacitySets,
    overallDifficulty = overallDifficulty,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun TrainingEntry.toDto(): TrainingEntryDto = TrainingEntryDto(
    id = id,
    code = buildTrainingCode(),
    weekNumber = weekNumber,
    maximumCapacitySets = maximumCapacitySets,
    overallDifficulty = overallDifficulty,
    createdAt = createdAt,
    updatedAt = updatedAt
)

private fun TrainingEntry.buildTrainingCode(): String = program.code + "_" + trainingDayNumber

private val TrainingEntryDto.trainingProgram: TrainingProgram
    get() = when {
        code.startsWith(TrainingProgram.UpperLower.code) -> TrainingProgram.UpperLower
        else -> TrainingProgram.Deload
    }

private val TrainingEntryDto.trainingDayNumber: Int
    get() = code.substringAfterLast("_").toIntOrNull() ?: 0
