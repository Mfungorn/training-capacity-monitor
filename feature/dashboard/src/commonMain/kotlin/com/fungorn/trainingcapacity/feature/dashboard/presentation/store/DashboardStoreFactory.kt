package com.fungorn.trainingcapacity.feature.dashboard.presentation.store

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.fungorn.trainingcapacity.feature.dashboard.domain.model.DashboardData
import com.fungorn.trainingcapacity.feature.dashboard.domain.usecase.GetDashboardDataUseCase
import com.fungorn.trainingcapacity.feature.dashboard.presentation.graph.DashboardDifficultyGraphData
import com.fungorn.trainingcapacity.feature.dashboard.presentation.graph.DashboardDifficultyGraphEntry
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DashboardStoreFactory(
    private val storeFactory: StoreFactory,
    private val getDashboardDataUseCase: GetDashboardDataUseCase,
) {

    fun create(): DashboardStore =
        object : DashboardStore, Store<DashboardStore.Intent, DashboardStore.State, DashboardStore.Label> by storeFactory.create(
            name = "DashboardStore",
            initialState = DashboardStore.State(),
            bootstrapper = BootstrapperImpl(),
            executorFactory = { ExecutorImpl() },
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data object StartLoading : Action
        data class DataLoaded(val data: DashboardData) : Action
        data class LoadingFailed(val error: String) : Action
    }

    private sealed interface Msg {
        data object StartLoading : Msg
        data class DataLoaded(val data: DashboardData) : Msg
        data class LoadingFailed(val error: String) : Msg
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            dispatch(Action.StartLoading)
            getDashboardDataUseCase()
                .onEach { data -> dispatch(Action.DataLoaded(data)) }
                .catch { e -> dispatch(Action.LoadingFailed(e.message ?: "Unknown error")) }
                .launchIn(scope)
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<DashboardStore.Intent, Action, DashboardStore.State, Msg, DashboardStore.Label>() {
        override fun executeAction(action: Action) {
            when (action) {
                is Action.StartLoading -> dispatch(Msg.StartLoading)
                is Action.DataLoaded -> dispatch(Msg.DataLoaded(action.data))
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
                        .onEach { data -> dispatch(Msg.DataLoaded(data)) }
                        .catch { e -> dispatch(Msg.LoadingFailed(e.message ?: "Unknown error")) }
                        .launchIn(scope)
                }
            }
        }
    }

    private object ReducerImpl : Reducer<DashboardStore.State, Msg> {

        override fun DashboardStore.State.reduce(msg: Msg): DashboardStore.State =
            when (msg) {
                is Msg.StartLoading -> copy(isLoading = true, error = null)
                is Msg.DataLoaded -> {
                    val data = msg.data
                    val graphEntries = data.difficultyGraphEntries.map {
                        DashboardDifficultyGraphEntry(
                            formattedDate = it.formattedDate,
                            difficulty = it.difficulty
                        )
                    }
                    copy(
                        currentMesocycleName = data.mesocycleName,
                        currentMesocycleWeekNumber = data.currentWeekNumber,
                        currentMesocycleWeekCapacityRange = data.currentWeekCapacityRange,
                        totalMesocycleWeeksCount = data.totalWeeksCount,
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
                        isLoading = false,
                        error = null
                    )
                }

                is Msg.LoadingFailed -> copy(isLoading = false, error = msg.error)
            }
    }
}
