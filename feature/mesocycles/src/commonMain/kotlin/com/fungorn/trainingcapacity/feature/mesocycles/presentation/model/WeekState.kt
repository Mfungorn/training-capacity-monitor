package com.fungorn.trainingcapacity.feature.mesocycles.presentation.model

import androidx.compose.runtime.Stable
import com.fungorn.trainingcapacity.core.domain.model.TrainingMesocycle

@Stable
data class WeekState(
    val type: TrainingMesocycle.Week.Type,
    val rirRange: RirRange
)