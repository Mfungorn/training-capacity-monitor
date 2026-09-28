package com.fungorn.trainingcapacity.core.domain.model

data class TrainingDay(
    val id: String,
    val name: String,
    val muscleGroups: Set<TrainingGroup>
)
