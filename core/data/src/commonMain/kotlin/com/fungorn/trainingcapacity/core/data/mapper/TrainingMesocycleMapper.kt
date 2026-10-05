package com.fungorn.trainingcapacity.core.data.mapper

import com.fungorn.trainingcapacity.core.data.local.dto.TrainingMesocycleDto
import com.fungorn.trainingcapacity.core.domain.model.GroupPriority
import com.fungorn.trainingcapacity.core.domain.model.TrainingMesocycle
import com.fungorn.trainingcapacity.core.domain.model.TrainingProgram

fun TrainingMesocycleDto.toDomain(program: TrainingProgram): TrainingMesocycle = TrainingMesocycle(
    id = id,
    program = program,
    weeks = weeks.map { weekDto ->
        weekDto.toDomain()
    },
    isSelected = isSelected,
    startedAt = startedAt,
    groupPriorities = groupPriorities.entries.mapNotNull { (groupName, priorityName) ->
        val group = groupName.toTrainingGroupOrNull() ?: return@mapNotNull null
        val priority = runCatching { GroupPriority.valueOf(priorityName) }.getOrNull()
            ?: return@mapNotNull null
        group to priority
    }.toMap(),
)

fun TrainingMesocycle.toDto(): TrainingMesocycleDto = TrainingMesocycleDto(
    id = id,
    programCode = program.code,
    weeks = weeks.map { week ->
        week.toDto()
    },
    isSelected = isSelected,
    startedAt = startedAt,
    groupPriorities = groupPriorities.entries.associate { (group, priority) ->
        group.name to priority.name
    },
)

private fun TrainingMesocycleDto.WeekDto.toDomain(): TrainingMesocycle.Week =
    TrainingMesocycle.Week(
        type = runCatching {
            TrainingMesocycle.Week.Type.valueOf(name.uppercase())
        }.getOrDefault(TrainingMesocycle.Week.Type.DELOAD),
        maximumCapacityLowerEnd = maximumCapacityLowerEnd,
        maximumCapacityUpperEnd = maximumCapacityUpperEnd,
    )

private fun TrainingMesocycle.Week.toDto(): TrainingMesocycleDto.WeekDto =
    TrainingMesocycleDto.WeekDto(
        name = type.name,
        maximumCapacityLowerEnd = maximumCapacityLowerEnd,
        maximumCapacityUpperEnd = maximumCapacityUpperEnd,
    )
