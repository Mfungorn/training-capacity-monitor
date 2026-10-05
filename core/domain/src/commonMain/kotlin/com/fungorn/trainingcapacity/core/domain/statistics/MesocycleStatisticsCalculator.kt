package com.fungorn.trainingcapacity.core.domain.statistics

import com.fungorn.trainingcapacity.core.domain.model.MesocycleStatistics
import com.fungorn.trainingcapacity.core.domain.model.SessionStatistics
import com.fungorn.trainingcapacity.core.domain.model.TrainingEntry
import com.fungorn.trainingcapacity.core.domain.model.TrainingGroup
import com.fungorn.trainingcapacity.core.domain.model.TrainingMesocycle
import com.fungorn.trainingcapacity.core.domain.model.WeekStatistics

object MesocycleStatisticsCalculator {

    fun calculate(mesocycle: TrainingMesocycle, entries: List<TrainingEntry>): MesocycleStatistics {
        val mesocycleEntries = entries
            .filter { it.mesocycleId == mesocycle.id }
            .sortedBy(TrainingEntry::createdAt)
        val lastWeekIndex = (mesocycle.weeks.size - 1).coerceAtLeast(0)
        val entriesByWeek = mesocycleEntries.groupBy { it.weekNumber.coerceIn(0, lastWeekIndex) }
        val plannedPerWeek = mesocycle.program.trainingDaysCount

        val weeks = mesocycle.weeks.mapIndexed { index, week ->
            WeekStatistics(
                weekNumber = index + 1,
                week = week,
                sessions = summarize(entriesByWeek[index].orEmpty(), plannedPerWeek),
            )
        }

        return MesocycleStatistics(
            mesocycle = mesocycle,
            weeks = weeks,
            sessions = summarize(mesocycleEntries, mesocycle.plannedSessions),
            entries = mesocycleEntries,
        )
    }

    fun summarize(entries: List<TrainingEntry>, plannedSessions: Int): SessionStatistics {
        val completed = entries.filter(TrainingEntry::isCompleted)
        return SessionStatistics(
            plannedSessions = plannedSessions,
            completedSessions = completed.size,
            skippedSessions = entries.size - completed.size,
            totalDurationMinutes = completed.sumOf(TrainingEntry::durationMinutes),
            spentMaximumCapacitySets = completed.sumOf(TrainingEntry::spentMaximumCapacitySets),
            averageDifficulty = completed.map(TrainingEntry::overallDifficulty).averageOrNull(),
            averageReadiness = completed.mapNotNull(TrainingEntry::readiness).averageOrNull(),
            averageFatigue = completed.mapNotNull(TrainingEntry::fatigue).averageOrNull(),
            muscleGroupFatigue = averageMuscleGroupFatigue(completed),
        )
    }

    private fun averageMuscleGroupFatigue(entries: List<TrainingEntry>): Map<TrainingGroup, Float> =
        entries.flatMap { it.muscleGroupFatigue.entries }
            .groupBy({ it.key }, { it.value })
            .mapValues { (_, values) -> values.average().toFloat() }

    private fun List<Int>.averageOrNull(): Float? = if (isEmpty()) null else average().toFloat()
}
