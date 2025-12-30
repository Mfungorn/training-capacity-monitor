package com.fungorn.trainingcapacity.core.domain.model

data class TrainingEntry(
    val id: String,
    val program: TrainingProgram,
    val trainingDayNumber: Int,
    val weekNumber: Int,
    val maximumCapacitySets: Int,
    val overallDifficulty: Int,
    val createdAt: Long,
    val updatedAt: Long
)
