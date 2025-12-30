package com.fungorn.trainingcapacity.feature.dashboard.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.fungorn.trainingcapacity.core.domain.model.TrainingEntry
import com.fungorn.trainingcapacity.core.domain.usecase.DeleteEntryUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetAllEntriesUseCase
import com.fungorn.trainingcapacity.feature.dashboard.store.DashboardStore.Intent
import com.fungorn.trainingcapacity.feature.dashboard.store.DashboardStore.Label
import com.fungorn.trainingcapacity.feature.dashboard.store.DashboardStore.State
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class DashboardStoreFactory(
    private val storeFactory: StoreFactory,
    private val getAllEntriesUseCase: GetAllEntriesUseCase,
    private val deleteEntryUseCase: DeleteEntryUseCase
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
        data class EntriesLoaded(val entries: List<TrainingEntry>) : Action
        data class LoadingFailed(val error: String) : Action
    }
    
    private sealed interface Msg {
        data object StartLoading : Msg
        data class EntriesLoaded(val entries: List<TrainingEntry>) : Msg
        data class LoadingFailed(val error: String) : Msg
    }
    
    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            dispatch(Action.StartLoading)
            getAllEntriesUseCase()
                .onEach { entries -> dispatch(Action.EntriesLoaded(entries)) }
                .catch { e -> dispatch(Action.LoadingFailed(e.message ?: "Unknown error")) }
                .launchIn(scope)
        }
    }
    
    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeAction(action: Action) {
            when (action) {
                is Action.StartLoading -> dispatch(Msg.StartLoading)
                is Action.EntriesLoaded -> dispatch(Msg.EntriesLoaded(action.entries))
                is Action.LoadingFailed -> {
                    dispatch(Msg.LoadingFailed(action.error))
                    publish(Label.ShowError(action.error))
                }
            }
        }
        
        override fun executeIntent(intent: Intent) {
            when (intent) {
                is Intent.LoadEntries -> {
                    dispatch(Msg.StartLoading)
                    getAllEntriesUseCase()
                        .onEach { entries -> dispatch(Msg.EntriesLoaded(entries)) }
                        .catch { e -> 
                            dispatch(Msg.LoadingFailed(e.message ?: "Unknown error"))
                            publish(Label.ShowError(e.message ?: "Unknown error"))
                        }
                        .launchIn(scope)
                }
                is Intent.DeleteEntry -> {
                    scope.launch {
                        try {
                            deleteEntryUseCase(intent.id)
                        } catch (e: Exception) {
                            publish(Label.ShowError(e.message ?: "Failed to delete entry"))
                        }
                    }
                }
            }
        }
    }
    
    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.StartLoading -> copy(isLoading = true, error = null)
                is Msg.EntriesLoaded -> copy(entries = msg.entries, isLoading = false)
                is Msg.LoadingFailed -> copy(isLoading = false, error = msg.error)
            }
    }
}
