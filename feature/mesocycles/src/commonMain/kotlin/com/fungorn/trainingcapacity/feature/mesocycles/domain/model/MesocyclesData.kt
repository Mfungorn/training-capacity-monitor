package com.fungorn.trainingcapacity.feature.mesocycles.domain.model

data class MesocyclesData(
    val currentMesocycle: MesocycleItem?,
    val previousMesocycles: List<MesocycleItem> = emptyList()
)
