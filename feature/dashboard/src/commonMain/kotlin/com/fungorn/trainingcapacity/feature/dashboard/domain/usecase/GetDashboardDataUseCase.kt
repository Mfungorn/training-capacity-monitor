package com.fungorn.trainingcapacity.feature.dashboard.domain.usecase

import com.fungorn.trainingcapacity.core.common.formatMillisToDate
import com.fungorn.trainingcapacity.core.domain.model.SessionStatistics
import com.fungorn.trainingcapacity.core.domain.model.TrainingEntry
import com.fungorn.trainingcapacity.core.domain.model.TrainingMesocycle
import com.fungorn.trainingcapacity.core.domain.repository.EntryRepository
import com.fungorn.trainingcapacity.core.domain.repository.MesocycleRepository
import com.fungorn.trainingcapacity.core.domain.statistics.MesocycleStatisticsCalculator
import com.fungorn.trainingcapacity.feature.dashboard.domain.model.DashboardData
import com.fungorn.trainingcapacity.feature.dashboard.domain.model.DifficultyGraphEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetDashboardDataUseCase(
    private val mesocycleRepository: MesocycleRepository,
    private val entryRepository: EntryRepository
) {
    operator fun invoke(): Flow<DashboardData?> =
        mesocycleRepository.getSelectedCycle()
            .combine(entryRepository.getAllEntries()) { cycle, entries ->
                cycle?.let { mapToDashboardData(it, entries) }
            }

    private fun mapToDashboardData(
        mesocycle: TrainingMesocycle,
        entries: List<TrainingEntry>
    ): DashboardData {
        val statistics = MesocycleStatisticsCalculator.calculate(mesocycle, entries)
        val mesocycleEntries = statistics.entries

        val currentProgram = mesocycle.program

        val completedTrainingDays = mesocycleEntries.size
        val trainingDaysPerWeek = currentProgram.trainingDaysCount

        val currentWeekIndex = if (trainingDaysPerWeek > 0) {
            completedTrainingDays / trainingDaysPerWeek
        } else {
            0
        }.coerceIn(0, mesocycle.weeks.size - 1)

        val completedDaysThisWeek = completedTrainingDays % trainingDaysPerWeek.coerceAtLeast(1)
        val nextTrainingDayNumber = completedDaysThisWeek + 1

        val currentMesocycleWeek = mesocycle.weeks.getOrNull(currentWeekIndex)
        val currentWeekSessions = statistics.weeks.getOrNull(currentWeekIndex)?.sessions
            ?: SessionStatistics.EMPTY

        val graphEntries = mesocycleEntries.asSequence()
            .filter(TrainingEntry::isCompleted)
            .map {
                DifficultyGraphEntry(
                    formattedDate = formatMillisToDate(it.createdAt),
                    difficulty = it.overallDifficulty
                )
            }
            .toList()

        return DashboardData(
            mesocycleId = mesocycle.id,
            mesocycleName = "${mesocycle.weeks.size}w cycle",
            currentWeekNumber = currentWeekIndex + 1, // 1-indexed for display
            currentWeekCapacityRange = currentMesocycleWeek?.let {
                "${it.maximumCapacityLowerEnd}-${it.maximumCapacityUpperEnd}"
            } ?: "-",
            isFirstWeek = currentWeekIndex == 0,
            isLastWeek = currentWeekIndex == mesocycle.weeks.size - 1,
            totalWeeksCount = mesocycle.weeks.size,
            spentMesocycleMaximumCapacitySets = statistics.sessions.spentMaximumCapacitySets,
            totalMesocycleMaximumCapacitySets = mesocycle.weeks.sumOf(
                TrainingMesocycle.Week::maximumCapacityUpperEnd
            ),
            currentProgramName = currentProgram.code,
            currentTrainingDayNumber = nextTrainingDayNumber,
            totalTrainingDaysCount = currentProgram.trainingDaysCount,
            spentMaximumCapacitySetsThisWeek = currentWeekSessions.spentMaximumCapacitySets,
            totalMaximumCapacitySetsThisWeek = currentMesocycleWeek?.maximumCapacityUpperEnd ?: 0,
            difficultyGraphEntries = graphEntries,
            mesocycleSessions = statistics.sessions,
            currentWeekSessions = currentWeekSessions,
            focusGroups = mesocycle.focusGroups,
        )
    }
}
