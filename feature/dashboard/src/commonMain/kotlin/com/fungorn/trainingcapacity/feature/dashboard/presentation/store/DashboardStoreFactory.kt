package com.fungorn.trainingcapacity.feature.dashboard.presentation.store

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.fungorn.trainingcapacity.core.domain.usecase.GetSelectedProgramUseCase
import com.fungorn.trainingcapacity.feature.dashboard.domain.model.DashboardData
import com.fungorn.trainingcapacity.feature.dashboard.domain.usecase.GetDashboardDataUseCase
import com.fungorn.trainingcapacity.feature.dashboard.presentation.graph.DashboardDifficultyGraphData
import com.fungorn.trainingcapacity.feature.dashboard.presentation.graph.DashboardDifficultyGraphEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardStoreFactory(
    private val storeFactory: StoreFactory,
    private val getDashboardDataUseCase: GetDashboardDataUseCase,
    private val getSelectedProgramUseCase: GetSelectedProgramUseCase,
) {

    fun create(): DashboardStore =
        object : DashboardStore,
            Store<DashboardStore.Intent, DashboardStore.State, DashboardStore.Label> by storeFactory.create(
                name = "DashboardStore",
                initialState = DashboardStore.State(),
                bootstrapper = BootstrapperImpl(),
                executorFactory = { ExecutorImpl() },
                reducer = ReducerImpl
            ) {}

    private sealed interface Action {
        data object StartLoading : Action
        data class DataLoaded(val data: DashboardData?) : Action
        data class SelectedProgramLoaded(val hasSelectedProgram: Boolean) : Action
        data class LoadingFailed(val error: String) : Action
    }

    private sealed interface Msg {
        data object StartLoading : Msg
        data object StartRefreshing : Msg
        data class DataLoaded(val data: DashboardData?) : Msg
        data class SelectedProgramLoaded(val hasSelectedProgram: Boolean) : Msg
        data class LoadingFailed(val error: String) : Msg
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            dispatch(Action.StartLoading)
            getDashboardDataUseCase()
                .flowOn(Dispatchers.IO)
                .onEach { data -> dispatch(Action.DataLoaded(data)) }
                .catch { e -> dispatch(Action.LoadingFailed(e.message ?: "Unknown error")) }
                .launchIn(scope)

            getSelectedProgramUseCase()
                .flowOn(Dispatchers.IO)
                .onEach { program -> dispatch(Action.SelectedProgramLoaded(program != null)) }
                .catch { /* ignore */ }
                .launchIn(scope)
        }
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<DashboardStore.Intent, Action, DashboardStore.State, Msg, DashboardStore.Label>() {
        override fun executeAction(action: Action) {
            when (action) {
                is Action.StartLoading -> dispatch(Msg.StartLoading)
                is Action.DataLoaded -> dispatch(Msg.DataLoaded(action.data))
                is Action.SelectedProgramLoaded -> dispatch(Msg.SelectedProgramLoaded(action.hasSelectedProgram))
                is Action.LoadingFailed -> {
                    dispatch(Msg.LoadingFailed(action.error))
                    publish(DashboardStore.Label.ShowError(action.error))
                }
            }
        }

        override fun executeIntent(intent: DashboardStore.Intent) {
            when (intent) {
                is DashboardStore.Intent.LoadEntries -> {
                    getDashboardDataUseCase()
                        .flowOn(Dispatchers.IO)
                        .onEach { data -> dispatch(Msg.DataLoaded(data)) }
                        .catch { e -> dispatch(Msg.LoadingFailed(e.message ?: "Unknown error")) }
                        .launchIn(scope)
                }

                is DashboardStore.Intent.Refresh -> {
                    dispatch(Msg.StartRefreshing)
                    scope.launch {
                        try {
                            delay(500L)
                            val data = withContext(Dispatchers.IO) {
                                getDashboardDataUseCase().first()
                            }
                            dispatch(Msg.DataLoaded(data))
                        } catch (e: Exception) {
                            dispatch(Msg.LoadingFailed(e.message ?: "Unknown error"))
                        }
                    }
                }
            }
        }
    }

    private object ReducerImpl : Reducer<DashboardStore.State, Msg> {

        override fun DashboardStore.State.reduce(msg: Msg): DashboardStore.State =
            when (msg) {
                is Msg.StartLoading -> copy(isLoading = true, isRefreshing = false, error = null)
                is Msg.StartRefreshing -> copy(isRefreshing = true, error = null)
                is Msg.DataLoaded -> {
                    val data = msg.data
                    if (data == null) {
                        copy(
                            currentMesocycleId = null,
                            isLoading = false,
                            isRefreshing = false,
                            error = null
                        )
                    } else {
                        val graphEntries = data.difficultyGraphEntries.map {
                            DashboardDifficultyGraphEntry(
                                formattedDate = it.formattedDate,
                                difficulty = it.difficulty
                            )
                        }
                        copy(
                            currentMesocycleId = data.mesocycleId,
                            currentMesocycleName = data.mesocycleName,
                            currentMesocycleWeekNumber = data.currentWeekNumber,
                            currentMesocycleWeekCapacityRange = data.currentWeekCapacityRange,
                            totalMesocycleWeeksCount = data.totalWeeksCount,
                            spentMesocycleMaximumCapacitySets = data.spentMesocycleMaximumCapacitySets,
                            totalMesocycleMaximumCapacitySets = data.totalMesocycleMaximumCapacitySets,
                            isFirstWeek = data.isFirstWeek,
                            isLastWeek = data.isLastWeek,
                            currentProgramName = data.currentProgramName,
                            currentTrainingDayNumber = data.currentTrainingDayNumber,
                            totalTrainingDaysCount = data.totalTrainingDaysCount,
                            spentMaximumCapacitySetsThisWeek = data.spentMaximumCapacitySetsThisWeek,
                            totalMaximumCapacitySetsThisWeek = data.totalMaximumCapacitySetsThisWeek,
                            graphData = DashboardDifficultyGraphData(
                                entries = SnapshotStateList<DashboardDifficultyGraphEntry>().apply {
                                    addAll(graphEntries)
                                }
                            ),
                            mesocycleSessions = data.mesocycleSessions,
                            currentWeekSessions = data.currentWeekSessions,
                            focusGroups = data.focusGroups,
                            isLoading = false,
                            isRefreshing = false,
                            error = null
                        )
                    }
                }

                is Msg.SelectedProgramLoaded -> copy(hasSelectedProgram = msg.hasSelectedProgram)
                is Msg.LoadingFailed -> copy(isLoading = false, isRefreshing = false, error = msg.error)
            }
    }
}
