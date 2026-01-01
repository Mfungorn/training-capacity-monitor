package com.fungorn.trainingcapacity.feature.dashboard.store

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.util.fastSumBy
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.fungorn.trainingcapacity.core.domain.model.TrainingEntry
import com.fungorn.trainingcapacity.core.domain.model.TrainingMesocycle
import com.fungorn.trainingcapacity.core.domain.model.TrainingProgram
import com.fungorn.trainingcapacity.core.domain.usecase.GetActiveMesocycleUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetAllEntriesUseCase
import com.fungorn.trainingcapacity.core.ui.formatMillisToDate
import com.fungorn.trainingcapacity.feature.dashboard.graph.DashboardDifficultyGraphData
import com.fungorn.trainingcapacity.feature.dashboard.graph.DashboardDifficultyGraphEntry
import com.fungorn.trainingcapacity.feature.dashboard.store.DashboardStore.Intent
import com.fungorn.trainingcapacity.feature.dashboard.store.DashboardStore.Label
import com.fungorn.trainingcapacity.feature.dashboard.store.DashboardStore.Label.ShowError
import com.fungorn.trainingcapacity.feature.dashboard.store.DashboardStore.State
import com.fungorn.trainingcapacity.feature.dashboard.store.DashboardStoreFactory.Msg.LoadingFailed
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DashboardStoreFactory(
    private val storeFactory: StoreFactory,
    private val getActiveMesocycleUseCase: GetActiveMesocycleUseCase,
    private val getAllEntriesUseCase: GetAllEntriesUseCase,
) {

    fun create(): DashboardStore =
        object : DashboardStore, Store<Intent, State, Label> by storeFactory.create(
            name = "DashboardStore",
            initialState = State(),
            bootstrapper = BootstrapperImpl(),
            executorFactory = { ExecutorImpl() },
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data object StartLoading : Action
        data class StateLoaded(
            val mesocycle: TrainingMesocycle,
            val entries: List<TrainingEntry>
        ) : Action

        data class LoadingFailed(val error: String) : Action
    }

    private sealed interface Msg {
        data object StartLoading : Msg
        data class StateLoaded(
            val mesocycle: TrainingMesocycle,
            val entries: List<TrainingEntry>
        ) : Msg

        data class LoadingFailed(val error: String) : Msg
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            dispatch(Action.StartLoading)
            getActiveMesocycleUseCase()
                .combine(getAllEntriesUseCase()) { cycle, entries -> cycle to entries }
                .onEach { (cycle, entries) ->
                    if (cycle != null) {
                        dispatch(Action.StateLoaded(cycle, entries))
                    } else {
                        throw IllegalStateException("No active mesocycle")
                    }
                }
                .catch { e -> dispatch(Action.LoadingFailed(e.message ?: "Unknown error")) }
                .launchIn(scope)
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeAction(action: Action) {
            when (action) {
                is Action.StartLoading -> dispatch(Msg.StartLoading)
                is Action.StateLoaded -> dispatch(Msg.StateLoaded(action.mesocycle, action.entries))
                is Action.LoadingFailed -> {
                    dispatch(LoadingFailed(action.error))
                    publish(ShowError(action.error))
                }
            }
        }

        override fun executeIntent(intent: Intent) {
            when (intent) {
                is Intent.LoadEntries -> {
                    getActiveMesocycleUseCase()
                        .combine(getAllEntriesUseCase()) { cycle, entries -> cycle to entries }
                        .onEach { (cycle, entries) ->
                            if (cycle != null) {
                                dispatch(Msg.StateLoaded(cycle, entries))
                            } else {
                                throw IllegalStateException("No active mesocycle")
                            }
                        }
                        .catch { e -> dispatch(LoadingFailed(e.message ?: "Unknown error")) }
                        .launchIn(scope)
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {

        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.StartLoading -> copy(isLoading = true, error = null)
                is Msg.StateLoaded -> {
                    val currentCycle = msg.mesocycle
                    val entries = msg.entries
                    val latestEntry = entries.lastOrNull()
                    val currentWeekNumber = latestEntry?.weekNumber ?: 0
                    val currentMesocycleWeek = currentCycle.weeks
                        .getOrNull(currentWeekNumber)
                    val currentWeekEntries = entries
                        .filter { it.weekNumber == currentWeekNumber }
                    val currentProgram = latestEntry?.program ?: TrainingProgram.Deload
                    val graphEntries = entries.asSequence()
                        .sortedBy(TrainingEntry::createdAt)
                        .map {
                            DashboardDifficultyGraphEntry(
                                formattedDate = formatMillisToDate(it.createdAt),
                                difficulty = it.overallDifficulty
                            )
                        }
                        .toList()
                    copy(
                        currentMesocycleName = "${currentCycle.weeks.size}w cycle",
                        currentMesocycleWeekNumber = currentWeekNumber,
                        currentMesocycleWeekCapacityRange = currentMesocycleWeek?.let {
                            "${it.maximumCapacityLowerEnd}-${it.maximumCapacityUpperEnd}"
                        } ?: "-",
                        totalMesocycleWeeksCount = currentCycle.weeks.size,
                        totalMesocycleMaximumCapacitySets = currentCycle.weeks.fastSumBy(
                            TrainingMesocycle.Week::maximumCapacityUpperEnd
                        ),
                        isFirstWeek = currentWeekNumber == 0,
                        isLastWeek = currentWeekNumber == currentCycle.weeks.size - 1,
                        currentProgramName = currentProgram.code,
                        currentTrainingDayNumber = latestEntry?.trainingDayNumber ?: 0,
                        totalTrainingDaysCount = currentProgram.trainingDaysCount,
                        spentMaximumCapacitySetsThisWeek = currentWeekEntries.fastSumBy(
                            TrainingEntry::spentMaximumCapacitySets
                        ),
                        totalMaximumCapacitySetsThisWeek = currentMesocycleWeek
                            ?.maximumCapacityUpperEnd ?: 0,
                        graphData = DashboardDifficultyGraphData(
                            entries = SnapshotStateList<DashboardDifficultyGraphEntry>().apply {
                                addAll(graphEntries)
                            }
                        ),
                        isLoading = false,
                        error = null
                    )
                }

                is LoadingFailed -> copy(isLoading = false, error = msg.error)
            }
    }
}
