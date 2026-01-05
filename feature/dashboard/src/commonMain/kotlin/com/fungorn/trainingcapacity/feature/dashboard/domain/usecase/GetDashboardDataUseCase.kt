package com.fungorn.trainingcapacity.feature.dashboard.domain.usecase

import com.fungorn.trainingcapacity.core.domain.model.TrainingEntry
import com.fungorn.trainingcapacity.core.domain.model.TrainingMesocycle
import com.fungorn.trainingcapacity.core.domain.model.TrainingProgram
import com.fungorn.trainingcapacity.core.domain.repository.EntryRepository
import com.fungorn.trainingcapacity.core.domain.repository.MesocycleRepository
import com.fungorn.trainingcapacity.core.common.formatMillisToDate
import com.fungorn.trainingcapacity.feature.dashboard.domain.model.DashboardData
import com.fungorn.trainingcapacity.feature.dashboard.domain.model.DifficultyGraphEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetDashboardDataUseCase(
    private val mesocycleRepository: MesocycleRepository,
    private val entryRepository: EntryRepository
) {
    operator fun invoke(): Flow<DashboardData> =
        mesocycleRepository.getSelectedCycle()
            .combine(entryRepository.getAllEntries()) { cycle, entries ->
                if (cycle != null) {
                    mapToDashboardData(cycle, entries)
                } else {
                    throw IllegalStateException("No active mesocycle")
                }
            }

    private fun mapToDashboardData(
        mesocycle: TrainingMesocycle,
        entries: List<TrainingEntry>
    ): DashboardData {
        val latestEntry = entries.lastOrNull()
        val currentWeekNumber = latestEntry?.weekNumber ?: 0
        val currentMesocycleWeek = mesocycle.weeks.getOrNull(currentWeekNumber)
        val currentWeekEntries = entries.filter { it.weekNumber == currentWeekNumber }
        val currentProgram = latestEntry?.program ?: TrainingProgram.Deload

        val graphEntries = entries.asSequence()
            .sortedBy(TrainingEntry::createdAt)
            .map {
                DifficultyGraphEntry(
                    formattedDate = formatMillisToDate(it.createdAt),
                    difficulty = it.overallDifficulty
                )
            }
            .toList()

        return DashboardData(
            mesocycleName = "${mesocycle.weeks.size}w cycle",
            currentWeekNumber = currentWeekNumber,
            currentWeekCapacityRange = currentMesocycleWeek?.let {
                "${it.maximumCapacityLowerEnd}-${it.maximumCapacityUpperEnd}"
            } ?: "-",
            isFirstWeek = currentWeekNumber == 0,
            isLastWeek = currentWeekNumber == mesocycle.weeks.size - 1,
            totalWeeksCount = mesocycle.weeks.size,
            totalMesocycleMaximumCapacitySets = mesocycle.weeks.sumOf(
                TrainingMesocycle.Week::maximumCapacityUpperEnd
            ),
            currentProgramName = currentProgram.code,
            currentTrainingDayNumber = latestEntry?.trainingDayNumber ?: 0,
            totalTrainingDaysCount = currentProgram.trainingDaysCount,
            spentMaximumCapacitySetsThisWeek = currentWeekEntries.sumOf(
                TrainingEntry::spentMaximumCapacitySets
            ),
            totalMaximumCapacitySetsThisWeek = currentMesocycleWeek?.maximumCapacityUpperEnd ?: 0,
            difficultyGraphEntries = graphEntries
        )
    }
}
