package com.fungorn.trainingcapacity.core.domain.export

import com.fungorn.trainingcapacity.core.common.formatDecimal
import com.fungorn.trainingcapacity.core.common.formatMillisToIsoDate
import com.fungorn.trainingcapacity.core.domain.model.MesocycleStatistics
import com.fungorn.trainingcapacity.core.domain.model.SessionStatistics
import com.fungorn.trainingcapacity.core.domain.model.TrainingGroup

class MesocycleCsvExporter {

    fun toExportFile(statistics: MesocycleStatistics): ExportFile = ExportFile(
        name = "mesocycle_${formatMillisToIsoDate(statistics.mesocycle.startedAt)}_${
            statistics.mesocycle.id.take(
                8
            )
        }.csv",
        mimeType = MIME_TYPE,
        content = toCsv(statistics),
    )

    fun toCsv(statistics: MesocycleStatistics): String {
        val mesocycle = statistics.mesocycle
        val groups = statistics.entries
            .flatMap { it.muscleGroupFatigue.keys }
            .distinct()
            .sortedBy(TrainingGroup::ordinal)

        return buildString {
            appendSection(
                listOf("Mesocycle", mesocycle.id),
                listOf("Program", mesocycle.program.code, mesocycle.program.name),
                listOf("Started", formatMillisToIsoDate(mesocycle.startedAt)),
                listOf("Weeks", mesocycle.weeks.size),
                listOf("Training days per week", mesocycle.program.trainingDaysCount),
                listOf(
                    "Focus groups",
                    mesocycle.focusGroups.joinToString(
                        " | ",
                        transform = TrainingGroup::displayName
                    )
                ),
                listOf(
                    "Growth groups",
                    mesocycle.growthGroups.joinToString(
                        " | ",
                        transform = TrainingGroup::displayName
                    )
                ),
                listOf(
                    "Maintenance groups",
                    mesocycle.maintenanceGroups.joinToString(
                        " | ",
                        transform = TrainingGroup::displayName
                    )
                ),
                *summaryRows(statistics.sessions).toTypedArray(),
            )
            appendLine()
            appendRow(
                "Week",
                "Type",
                "RIR lower",
                "RIR upper",
                "Planned",
                "Completed",
                "Skipped",
                "Adherence",
                "Duration (min)",
                "Avg readiness",
                "Avg fatigue",
                "Max capacity sets",
                "Avg difficulty",
            )
            statistics.weeks.forEach { week ->
                val sessions = week.sessions
                appendRow(
                    week.weekNumber,
                    week.week.type.name,
                    week.week.maximumCapacityLowerEnd,
                    week.week.maximumCapacityUpperEnd,
                    sessions.plannedSessions,
                    sessions.completedSessions,
                    sessions.skippedSessions,
                    sessions.adherence.formatRatio(),
                    sessions.totalDurationMinutes,
                    sessions.averageReadiness.formatDecimal(),
                    sessions.averageFatigue.formatDecimal(),
                    sessions.spentMaximumCapacitySets,
                    sessions.averageDifficulty.formatDecimal(),
                )
            }
            appendLine()
            appendRow(
                *listOf(
                    "Date",
                    "Week",
                    "Day",
                    "Program",
                    "Completed",
                    "Duration (min)",
                    "Readiness",
                    "Fatigue",
                    "Difficulty",
                    "Max capacity sets",
                ).plus(groups.map { "Fatigue: ${it.displayName}" }).toTypedArray()
            )
            statistics.entries.forEach { entry ->
                appendRow(
                    *listOf<Any?>(
                        formatMillisToIsoDate(entry.createdAt),
                        entry.weekNumber + 1,
                        entry.trainingDayNumber,
                        entry.programId,
                        if (entry.isCompleted) "yes" else "no",
                        entry.durationMinutes,
                        entry.readiness,
                        entry.fatigue,
                        entry.overallDifficulty,
                        entry.spentMaximumCapacitySets,
                    ).plus(groups.map { entry.muscleGroupFatigue[it] }).toTypedArray()
                )
            }
        }
    }

    private fun summaryRows(sessions: SessionStatistics): List<List<Any?>> = listOf(
        listOf("Planned sessions", sessions.plannedSessions),
        listOf("Completed sessions", sessions.completedSessions),
        listOf("Skipped sessions", sessions.skippedSessions),
        listOf("Adherence", sessions.adherence.formatRatio()),
        listOf("Adherence to date", sessions.adherenceToDate.formatRatio()),
        listOf("Total duration (min)", sessions.totalDurationMinutes),
        listOf("Average session duration (min)", sessions.averageDurationMinutes.formatDecimal()),
        listOf("Average readiness", sessions.averageReadiness.formatDecimal()),
        listOf("Average fatigue", sessions.averageFatigue.formatDecimal()),
        listOf("Average difficulty", sessions.averageDifficulty.formatDecimal()),
        listOf("Max capacity sets", sessions.spentMaximumCapacitySets),
    )

    private fun StringBuilder.appendSection(vararg rows: List<Any?>) =
        rows.forEach { appendRow(*it.toTypedArray()) }

    private fun StringBuilder.appendRow(vararg cells: Any?) {
        appendLine(cells.joinToString(SEPARATOR) { it.toCsvCell() })
    }

    private fun Any?.toCsvCell(): String {
        val text = this?.toString() ?: ""
        val needsQuoting = text.any { it == ',' || it == '"' || it == '\n' || it == '\r' }
        return if (needsQuoting) "\"${text.replace("\"", "\"\"")}\"" else text
    }

    private fun Float?.formatRatio(): String? = this?.let { formatDecimal(it * 100) + "%" }

    private fun Float?.formatDecimal(): String? = this?.let(::formatDecimal)

    companion object {
        const val MIME_TYPE = "text/csv"
        private const val SEPARATOR = ","
    }
}
