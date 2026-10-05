package com.fungorn.trainingcapacity.core.domain.export

import com.fungorn.trainingcapacity.core.domain.model.GroupPriority
import com.fungorn.trainingcapacity.core.domain.model.TrainingDay
import com.fungorn.trainingcapacity.core.domain.model.TrainingEntry
import com.fungorn.trainingcapacity.core.domain.model.TrainingGroup
import com.fungorn.trainingcapacity.core.domain.model.TrainingMesocycle
import com.fungorn.trainingcapacity.core.domain.model.TrainingProgram
import com.fungorn.trainingcapacity.core.domain.statistics.MesocycleStatisticsCalculator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MesocycleCsvExporterTest {

    private val mesocycle = TrainingMesocycle(
        id = "abcdef123456",
        program = TrainingProgram(
            code = "UL",
            name = "Upper, Lower",
            trainingDays = listOf(TrainingDay("U", "Upper", setOf(TrainingGroup.CHEST)))
        ),
        weeks = listOf(TrainingMesocycle.Week(TrainingMesocycle.Week.Type.WORKING, 2, 4)),
        isSelected = true,
        startedAt = 0L,
        groupPriorities = mapOf(TrainingGroup.CHEST to GroupPriority.FOCUS)
    )

    private val entries = listOf(
        TrainingEntry(
            id = "1",
            mesocycleId = mesocycle.id,
            programId = "UL",
            weekNumber = 0,
            trainingDayNumber = 1,
            spentMaximumCapacitySets = 3,
            overallDifficulty = 2,
            createdAt = 0L,
            durationMinutes = 65,
            readiness = 4,
            fatigue = 3,
            muscleGroupFatigue = mapOf(TrainingGroup.CHEST to 4),
        ),
        TrainingEntry(
            id = "2",
            mesocycleId = mesocycle.id,
            programId = "UL",
            weekNumber = 0,
            trainingDayNumber = 1,
            spentMaximumCapacitySets = 0,
            overallDifficulty = 0,
            createdAt = 1L,
            isCompleted = false,
        ),
    )

    private val exporter = MesocycleCsvExporter()

    @Test
    fun csvContainsSummaryWeeksAndEntries() {
        val csv = exporter.toCsv(MesocycleStatisticsCalculator.calculate(mesocycle, entries))
        val lines = csv.lines()

        assertTrue(lines.contains("Mesocycle,abcdef123456"))
        assertTrue(
            lines.contains("Program,UL,\"Upper, Lower\""),
            "program name with comma must be quoted"
        )
        assertTrue(lines.contains("Focus groups,Chest"))
        assertTrue(lines.contains("Completed sessions,1"))
        assertTrue(lines.contains("Skipped sessions,1"))
        assertTrue(lines.contains("Adherence,100.0%"))
        assertTrue(lines.contains("Adherence to date,50.0%"))
        assertTrue(lines.contains("Total duration (min),65"))
        assertTrue(lines.contains("1,WORKING,2,4,1,1,1,100.0%,65,4.0,3.0,3,2.0"))
        assertTrue(lines.any { it.startsWith("Date,Week,Day,Program,Completed") && it.endsWith("Fatigue: Chest") })
        assertTrue(lines.any { it.endsWith(",1,1,UL,yes,65,4,3,2,3,4") })
        assertTrue(lines.any { it.endsWith(",1,1,UL,no,0,,,0,0,") })
    }

    @Test
    fun exportFileUsesCsvNameAndMimeType() {
        val file =
            exporter.toExportFile(MesocycleStatisticsCalculator.calculate(mesocycle, entries))

        assertEquals("text/csv", file.mimeType)
        assertTrue(
            file.name.startsWith("mesocycle_") && file.name.endsWith("_abcdef12.csv"),
            file.name
        )
    }
}
