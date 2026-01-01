package com.fungorn.trainingcapacity.core.domain.model

data class TrainingMesocycle(
    val id: String,
    val weeks: List<Week>,
    val isSelected: Boolean,
    val startedAt: Long,
) {
    data class Week(
        val type: Type,
        val maximumCapacityLowerEnd: Int,
        val maximumCapacityUpperEnd: Int,
    ) {
        enum class Type {
            ADAPT,
            WORKING,
            DELOAD
        }
    }
}
