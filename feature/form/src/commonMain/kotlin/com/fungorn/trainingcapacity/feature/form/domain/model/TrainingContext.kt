package com.fungorn.trainingcapacity.feature.form.domain.model

import com.fungorn.trainingcapacity.core.domain.model.TrainingProgram

data class TrainingContext(
    val mesocycleId: String,
    val currentProgram: TrainingProgram,
    val currentWeekNumber: Int,
    val nextTrainingDayNumber: Int
)
