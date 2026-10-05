package com.fungorn.trainingcapacity.feature.form.domain.usecase

import com.fungorn.trainingcapacity.core.domain.repository.EntryRepository
import com.fungorn.trainingcapacity.core.domain.repository.MesocycleRepository
import com.fungorn.trainingcapacity.feature.form.domain.model.TrainingContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetCurrentTrainingContextUseCase(
    private val mesocycleRepository: MesocycleRepository,
    private val entryRepository: EntryRepository
) {
    operator fun invoke(): Flow<TrainingContext?> =
        mesocycleRepository.getSelectedCycle()
            .combine(entryRepository.getAllEntries()) { cycle, entries ->
                if (cycle == null) return@combine null

                // Filter entries for current mesocycle only
                val mesocycleEntries = entries.filter { it.mesocycleId == cycle.id }
                    .sortedBy { it.createdAt }

                val currentProgram = cycle.program
                val trainingDaysPerWeek = currentProgram.trainingDaysCount

                // Calculate current week based on completed training days
                val completedTrainingDays = mesocycleEntries.size
                val currentWeekNumber = if (trainingDaysPerWeek > 0) {
                    completedTrainingDays / trainingDaysPerWeek
                } else {
                    0
                }.coerceIn(0, cycle.weeks.size - 1)

                // Training day within current week
                val completedDaysThisWeek =
                    completedTrainingDays % trainingDaysPerWeek.coerceAtLeast(1)
                val nextTrainingDayNumber = completedDaysThisWeek + 1

                TrainingContext(
                    mesocycleId = cycle.id,
                    currentProgram = cycle.program,
                    currentWeekNumber = currentWeekNumber,
                    nextTrainingDayNumber = nextTrainingDayNumber
                )
            }
}
