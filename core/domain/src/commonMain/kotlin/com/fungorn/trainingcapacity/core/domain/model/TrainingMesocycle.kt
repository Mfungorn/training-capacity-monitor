package com.fungorn.trainingcapacity.core.domain.model

data class TrainingMesocycle(
    val id: String,
    val program: TrainingProgram,
    val weeks: List<Week>,
    val isSelected: Boolean,
    val startedAt: Long,
    val groupPriorities: Map<TrainingGroup, GroupPriority> = emptyMap(),
) {
    init {
        require(focusGroups.size <= MAX_FOCUS_GROUPS) {
            "A mesocycle can have at most $MAX_FOCUS_GROUPS focus groups"
        }
    }

    val focusGroups: Set<TrainingGroup> get() = groupsWithPriority(GroupPriority.FOCUS)
    val growthGroups: Set<TrainingGroup> get() = groupsWithPriority(GroupPriority.GROWTH)
    val maintenanceGroups: Set<TrainingGroup> get() = groupsWithPriority(GroupPriority.MAINTENANCE)

    val plannedSessions: Int get() = weeks.size * program.trainingDaysCount

    private fun groupsWithPriority(priority: GroupPriority): Set<TrainingGroup> =
        groupPriorities.filterValues { it == priority }.keys

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

    companion object {
        const val MAX_FOCUS_GROUPS = 2
    }
}
