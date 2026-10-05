package com.fungorn.trainingcapacity.core.domain.statistics

import com.fungorn.trainingcapacity.core.domain.model.GroupPriority
import com.fungorn.trainingcapacity.core.domain.model.TrainingDay
import com.fungorn.trainingcapacity.core.domain.model.TrainingEntry
import com.fungorn.trainingcapacity.core.domain.model.TrainingGroup
import com.fungorn.trainingcapacity.core.domain.model.TrainingMesocycle
import com.fungorn.trainingcapacity.core.domain.model.TrainingProgram
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class MesocycleStatisticsCalculatorTest {

    private val program = TrainingProgram(
        code = "ABC",
        name = "Three days",
        trainingDays = listOf(
            TrainingDay("A", "A", setOf(TrainingGroup.QUADS, TrainingGroup.SIDE_DELTS)),
            TrainingDay("B", "B", setOf(TrainingGroup.BACK, TrainingGroup.CHEST)),
            TrainingDay("C", "C", setOf(TrainingGroup.TRICEPS)),
        )
    )

    private val mesocycle = TrainingMesocycle(
        id = "meso",
        program = program,
        weeks = listOf(
            TrainingMesocycle.Week(TrainingMesocycle.Week.Type.ADAPT, 2, 4),
            TrainingMesocycle.Week(TrainingMesocycle.Week.Type.WORKING, 4, 6),
        ),
        isSelected = true,
        startedAt = 0L,
        groupPriorities = mapOf(
            TrainingGroup.TRICEPS to GroupPriority.FOCUS,
            TrainingGroup.SIDE_DELTS to GroupPriority.FOCUS,
            TrainingGroup.BACK to GroupPriority.GROWTH,
            TrainingGroup.CHEST to GroupPriority.MAINTENANCE,
        )
    )

    private fun entry(
        id: String,
        week: Int,
        day: Int,
        completed: Boolean = true,
        duration: Int = 60,
        readiness: Int? = 4,
        fatigue: Int? = 3,
        groupFatigue: Map<TrainingGroup, Int> = emptyMap(),
        mesocycleId: String = mesocycle.id,
    ) = TrainingEntry(
        id = id,
        mesocycleId = mesocycleId,
        programId = program.code,
        weekNumber = week,
        trainingDayNumber = day,
        spentMaximumCapacitySets = 2,
        overallDifficulty = 2,
        createdAt = id.hashCode().toLong(),
        isCompleted = completed,
        durationMinutes = duration,
        readiness = readiness,
        fatigue = fatigue,
        muscleGroupFatigue = groupFatigue,
    )

    @Test
    fun emptyMesocycleHasPlannedSessionsButNoAdherence() {
        val stats = MesocycleStatisticsCalculator.calculate(mesocycle, emptyList())

        assertEquals(6, stats.sessions.plannedSessions)
        assertEquals(0, stats.sessions.completedSessions)
        assertNull(stats.sessions.adherenceToDate)
        assertEquals(0f, stats.sessions.adherence)
        assertEquals(2, stats.weeks.size)
        assertEquals(3, stats.weeks[0].sessions.plannedSessions)
    }

    @Test
    fun adherenceCountsSkippedSessionsAgainstPlan() {
        val entries = listOf(
            entry("1", week = 0, day = 1),
            entry(
                "2",
                week = 0,
                day = 2,
                completed = false,
                duration = 0,
                readiness = null,
                fatigue = null
            ),
            entry("3", week = 0, day = 3),
            entry("4", week = 1, day = 1),
        )

        val stats = MesocycleStatisticsCalculator.calculate(mesocycle, entries)

        val firstWeek = stats.weeks[0].sessions
        assertEquals(2, firstWeek.completedSessions)
        assertEquals(1, firstWeek.skippedSessions)
        assertEquals(2f / 3f, firstWeek.adherence)
        assertEquals(2f / 3f, firstWeek.adherenceToDate)

        val secondWeek = stats.weeks[1].sessions
        assertEquals(1, secondWeek.completedSessions)
        assertEquals(1f / 3f, secondWeek.adherence)
        assertEquals(1f, secondWeek.adherenceToDate)

        assertEquals(3, stats.sessions.completedSessions)
        assertEquals(1, stats.sessions.skippedSessions)
        assertEquals(3f / 6f, stats.sessions.adherence)
        assertEquals(3f / 4f, stats.sessions.adherenceToDate)
    }

    @Test
    fun durationAndFeelingsIgnoreSkippedSessionsAndOtherMesocycles() {
        val entries = listOf(
            entry("1", week = 0, day = 1, duration = 50, readiness = 5, fatigue = 2),
            entry("2", week = 0, day = 2, duration = 70, readiness = 3, fatigue = 4),
            entry(
                "3",
                week = 0,
                day = 3,
                completed = false,
                duration = 99,
                readiness = 1,
                fatigue = 5
            ),
            entry("other", week = 0, day = 1, duration = 500, mesocycleId = "another"),
        )

        val stats = MesocycleStatisticsCalculator.calculate(mesocycle, entries)

        assertEquals(120, stats.sessions.totalDurationMinutes)
        assertEquals(60f, stats.sessions.averageDurationMinutes)
        assertEquals(4f, stats.sessions.averageReadiness)
        assertEquals(3f, stats.sessions.averageFatigue)
        assertEquals(3, stats.entries.size)
    }

    @Test
    fun muscleGroupFatigueIsAveragedPerGroup() {
        val entries = listOf(
            entry(
                "1",
                week = 0,
                day = 1,
                groupFatigue = mapOf(TrainingGroup.QUADS to 4, TrainingGroup.SIDE_DELTS to 2)
            ),
            entry("2", week = 1, day = 1, groupFatigue = mapOf(TrainingGroup.QUADS to 2)),
        )

        val stats = MesocycleStatisticsCalculator.calculate(mesocycle, entries)

        assertEquals(3f, stats.sessions.muscleGroupFatigue[TrainingGroup.QUADS])
        assertEquals(2f, stats.sessions.muscleGroupFatigue[TrainingGroup.SIDE_DELTS])
        assertEquals(4f, stats.weeks[0].sessions.muscleGroupFatigue[TrainingGroup.QUADS])
    }

    @Test
    fun entriesBeyondPlannedWeeksFallIntoLastWeek() {
        val stats = MesocycleStatisticsCalculator.calculate(
            mesocycle,
            listOf(entry("1", week = 7, day = 1))
        )

        assertEquals(1, stats.weeks[1].sessions.completedSessions)
    }

    @Test
    fun mesocycleExposesGroupsByPriorityAndLimitsFocus() {
        assertEquals(setOf(TrainingGroup.TRICEPS, TrainingGroup.SIDE_DELTS), mesocycle.focusGroups)
        assertEquals(setOf(TrainingGroup.BACK), mesocycle.growthGroups)
        assertEquals(setOf(TrainingGroup.CHEST), mesocycle.maintenanceGroups)

        assertFailsWith<IllegalArgumentException> {
            mesocycle.copy(groupPriorities = mesocycle.groupPriorities + (TrainingGroup.QUADS to GroupPriority.FOCUS))
        }
    }
}
