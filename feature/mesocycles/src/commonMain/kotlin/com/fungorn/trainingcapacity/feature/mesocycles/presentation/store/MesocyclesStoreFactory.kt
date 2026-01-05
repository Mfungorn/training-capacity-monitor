package com.fungorn.trainingcapacity.feature.mesocycles.presentation.store

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.fungorn.trainingcapacity.feature.mesocycles.domain.model.MesocycleItem
import com.fungorn.trainingcapacity.feature.mesocycles.domain.usecase.GetMesocycleListUseCase
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.model.MesocycleListItem
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MesocyclesStoreFactory(
    private val storeFactory: StoreFactory,
    private val getMesocycleListUseCase: GetMesocycleListUseCase
) {

    fun create(): MesocyclesStore =
        object : MesocyclesStore,
            Store<MesocyclesStore.Intent, MesocyclesStore.State, MesocyclesStore.Label> by storeFactory.create(
                name = "MesocyclesStore",
                initialState = MesocyclesStore.State(),
                bootstrapper = BootstrapperImpl(),
                executorFactory = ::ExecutorImpl,
                reducer = ReducerImpl
            ) {}

    private sealed interface Action {
        data class MesocyclesLoaded(val mesocycles: List<MesocycleItem>) : Action
        data class LoadError(val message: String) : Action
    }

    private sealed interface Msg {
        data class MesocyclesLoaded(
            val mesocycles: List<MesocycleItem>,
            val currentMesocycle: MesocycleItem?
        ) : Msg

        data class SetError(val message: String) : Msg
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            getMesocycleListUseCase()
                .onEach { mesocycles ->
                    dispatch(Action.MesocyclesLoaded(mesocycles))
                }
                .catch { e ->
                    dispatch(Action.LoadError(e.message ?: "Failed to load mesocycles"))
                }
                .launchIn(scope)
        }
    }

    private class ExecutorImpl :
        CoroutineExecutor<MesocyclesStore.Intent, Action, MesocyclesStore.State, Msg, MesocyclesStore.Label>() {

        override fun executeAction(action: Action) {
            when (action) {
                is Action.MesocyclesLoaded -> {
                    val currentMesocycle = action.mesocycles.find { it.isSelected }
                    val previousMesocycles = action.mesocycles.filter { !it.isSelected }
                    dispatch(Msg.MesocyclesLoaded(previousMesocycles, currentMesocycle))
                }

                is Action.LoadError -> dispatch(Msg.SetError(action.message))
            }
        }

        override fun executeIntent(intent: MesocyclesStore.Intent) {
            when (intent) {
                is MesocyclesStore.Intent.OpenMesocycle -> {
                    publish(MesocyclesStore.Label.NavigateToViewMesocycle(intent.id))
                }

                is MesocyclesStore.Intent.StartNewMesocycle -> {
                    publish(MesocyclesStore.Label.NavigateToCreateMesocycle)
                }
            }
        }
    }

    private object ReducerImpl : Reducer<MesocyclesStore.State, Msg> {
        override fun MesocyclesStore.State.reduce(msg: Msg): MesocyclesStore.State =
            when (msg) {
                is Msg.MesocyclesLoaded -> copy(
                    mesocycles = SnapshotStateList<MesocycleListItem>().apply {
                        addAll(
                            msg.mesocycles.map {
                                MesocycleListItem(
                                    id = it.id,
                                    title = it.title,
                                    startDateFormatted = it.startDateFormatted,
                                    isSelected = it.isSelected
                                )
                            }
                        )
                    },
                    currentMesocycle = msg.currentMesocycle?.let(::map),
                    isLoading = false
                )

                is Msg.SetError -> copy(
                    errorMessage = msg.message,
                    isLoading = false
                )
            }

        private fun map(item: MesocycleItem): MesocycleListItem = MesocycleListItem(
            id = item.id,
            title = item.title,
            startDateFormatted = item.startDateFormatted,
            isSelected = item.isSelected,
        )
    }
}
