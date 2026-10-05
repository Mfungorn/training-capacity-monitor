package com.fungorn.trainingcapacity.core.data.mapper

import com.fungorn.trainingcapacity.core.data.local.dto.TrainingEntryDto
import com.fungorn.trainingcapacity.core.domain.model.TrainingEntry
import com.fungorn.trainingcapacity.core.domain.model.TrainingGroup

fun TrainingEntryDto.toDomain(): TrainingEntry = TrainingEntry(
    id = id,
    mesocycleId = mesocycleId,
    programId = programId,
    trainingDayNumber = trainingDayNumber,
    weekNumber = weekNumber,
    spentMaximumCapacitySets = spentMaximumCapacitySets,
    overallDifficulty = overallDifficulty,
    createdAt = createdAt,
    isCompleted = isCompleted,
    durationMinutes = durationMinutes,
    readiness = readiness,
    fatigue = fatigue,
    muscleGroupFatigue = muscleGroupFatigue.entries.mapNotNull { (name, value) ->
        name.toTrainingGroupOrNull()?.let { it to value }
    }.toMap(),
)

fun TrainingEntry.toDto(): TrainingEntryDto = TrainingEntryDto(
    id = id,
    mesocycleId = mesocycleId,
    code = buildTrainingCode(),
    spentMaximumCapacitySets = spentMaximumCapacitySets,
    overallDifficulty = overallDifficulty,
    createdAt = createdAt,
    isCompleted = isCompleted,
    durationMinutes = durationMinutes,
    readiness = readiness,
    fatigue = fatigue,
    muscleGroupFatigue = muscleGroupFatigue.mapKeys { (group, _) -> group.name },
)

internal fun String.toTrainingGroupOrNull(): TrainingGroup? =
    runCatching { TrainingGroup.valueOf(this) }.getOrNull()

private fun TrainingEntry.buildTrainingCode(): String = programId +
        "_" + trainingDayNumber +
        "_" + weekNumber

private val TrainingEntryDto.programId: String
    get() = code.split("_").firstOrNull() ?: ""

private val TrainingEntryDto.trainingDayNumber: Int
    get() = code.split("_").drop(1).firstOrNull()?.toIntOrNull() ?: 0

private val TrainingEntryDto.weekNumber: Int
    get() = code.substringAfterLast("_").toIntOrNull() ?: 0
