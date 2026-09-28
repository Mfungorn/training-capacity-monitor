package com.fungorn.trainingcapacity.core.data.mapper

import com.fungorn.trainingcapacity.core.data.local.dto.TrainingDayDto
import com.fungorn.trainingcapacity.core.data.local.dto.TrainingProgramDto
import com.fungorn.trainingcapacity.core.domain.model.TrainingDay
import com.fungorn.trainingcapacity.core.domain.model.TrainingGroup
import com.fungorn.trainingcapacity.core.domain.model.TrainingProgram

fun TrainingProgramDto.toDomain(): TrainingProgram = TrainingProgram(
    code = code,
    name = name,
    trainingDays = trainingDays.map { it.toDomain() }
)

fun TrainingDayDto.toDomain(): TrainingDay = TrainingDay(
    id = id,
    name = name,
    muscleGroups = muscleGroups.mapNotNull { groupName ->
        runCatching { TrainingGroup.valueOf(groupName) }.getOrNull()
    }.toSet()
)

fun TrainingProgram.toDto(): TrainingProgramDto = TrainingProgramDto(
    code = code,
    name = name,
    trainingDays = trainingDays.map { it.toDto() },
    isBuiltIn = false
)

fun TrainingDay.toDto(): TrainingDayDto = TrainingDayDto(
    id = id,
    name = name,
    muscleGroups = muscleGroups.map(TrainingGroup::name)
)
