package com.fungorn.trainingcapacity.core.domain.model

sealed class TrainingProgram(
    val code: String,
    val trainingDaysCount: Int
) {
    data object Deload : TrainingProgram(
        code = "DL0",
        trainingDaysCount = 2
    )

    data object UpperLower : TrainingProgram(
        code = "UL1",
        trainingDaysCount = Day.entries.size
    ) {
        enum class Day(
            val groups: Set<TrainingGroup>
        ) {
            UPPER_1(
                setOf(
                    TrainingGroup.CHEST,
                    TrainingGroup.BACK,
                    TrainingGroup.DELTS,
                    TrainingGroup.BICEPS,
                    TrainingGroup.TRICEPS,
                )
            ),
            LOWER_1(
                setOf(
                    TrainingGroup.LEGS,
                    TrainingGroup.DELTS,
                    TrainingGroup.BICEPS,
                    TrainingGroup.TRICEPS,
                )
            ),
            UPPER_2(
                setOf(
                    TrainingGroup.CHEST,
                    TrainingGroup.BACK,
                    TrainingGroup.DELTS,
                    TrainingGroup.BICEPS,
                    TrainingGroup.TRICEPS,
                )
            ),
            LOWER_2(
                setOf(
                    TrainingGroup.LEGS,
                    TrainingGroup.DELTS,
                    TrainingGroup.BICEPS,
                    TrainingGroup.TRICEPS,
                )
            )
        }
    }
}