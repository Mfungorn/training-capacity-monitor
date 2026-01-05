package com.fungorn.trainingcapacity.feature.mesocycles.presentation.model

import androidx.compose.runtime.Stable

@Stable
data class MesocycleListItem(
    val id: String,
    val title: String,
    val startDateFormatted: String,
    val isSelected: Boolean
)
