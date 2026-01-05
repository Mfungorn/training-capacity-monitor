package com.fungorn.trainingcapacity.feature.dashboard.presentation.graph

import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshots.SnapshotStateList

@Stable
data class DashboardDifficultyGraphData(
    val entries: SnapshotStateList<DashboardDifficultyGraphEntry> = SnapshotStateList()
)

@Stable
data class DashboardDifficultyGraphEntry(
    val formattedDate: String,
    val difficulty: Int
)
