package com.fungorn.trainingcapacity.core.domain.model

data class TrainingEntry(
    val id: String,
    val program: TrainingProgram,
    val weekNumber: Int,
    val trainingDayNumber: Int,
    val spentMaximumCapacitySets: Int,
    val overallDifficulty: Int,
    val createdAt: Long,
)
