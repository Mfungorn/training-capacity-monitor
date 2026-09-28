package com.fungorn.trainingcapacity.core.domain.model

data class TrainingProgram(
    val code: String,
    val name: String,
    val trainingDays: List<TrainingDay>
) {
    val trainingDaysCount: Int get() = trainingDays.size
}