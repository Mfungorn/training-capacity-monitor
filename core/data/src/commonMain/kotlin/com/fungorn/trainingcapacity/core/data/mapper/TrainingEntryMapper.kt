package com.fungorn.trainingcapacity.core.data.mapper

import com.fungorn.trainingcapacity.core.data.local.dto.TrainingEntryDto
import com.fungorn.trainingcapacity.core.domain.model.TrainingEntry
import com.fungorn.trainingcapacity.core.domain.model.TrainingProgram

fun TrainingEntryDto.toDomain(): TrainingEntry = TrainingEntry(
    id = id,
    program = trainingProgram,
    trainingDayNumber = trainingDayNumber,
    weekNumber = weekNumber,
    spentMaximumCapacitySets = spentMaximumCapacitySets,
    overallDifficulty = overallDifficulty,
    createdAt = createdAt,
)

fun TrainingEntry.toDto(): TrainingEntryDto = TrainingEntryDto(
    id = id,
    code = buildTrainingCode(),
    spentMaximumCapacitySets = spentMaximumCapacitySets,
    overallDifficulty = overallDifficulty,
    createdAt = createdAt,
)

private fun TrainingEntry.buildTrainingCode(): String = program.code +
        "_" + trainingDayNumber +
        "_" + weekNumber

private val TrainingEntryDto.trainingProgram: TrainingProgram
    get() = when {
        code.startsWith(TrainingProgram.UpperLower.code) -> TrainingProgram.UpperLower
        else -> TrainingProgram.Deload
    }

private val TrainingEntryDto.trainingDayNumber: Int
    get() = code.split("_").drop(1).firstOrNull()?.toIntOrNull() ?: 0

private val TrainingEntryDto.weekNumber: Int
    get() = code.substringAfterLast("_").toIntOrNull() ?: 0
