package com.fungorn.trainingcapacity.feature.programs.presentation.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.fungorn.trainingcapacity.core.common.DispatcherProvider
import com.fungorn.trainingcapacity.core.domain.model.TrainingProgram
import com.fungorn.trainingcapacity.core.domain.usecase.GetAllProgramsUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetSelectedProgramUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.SelectProgramUseCase
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProgramsStoreFactory(
    private val storeFactory: StoreFactory,
    private val dispatcherProvider: DispatcherProvider,
    private val getAllProgramsUseCase: GetAllProgramsUseCase,
    private val getSelectedProgramUseCase: GetSelectedProgramUseCase,
    private val selectProgramUseCase: SelectProgramUseCase
) {

    fun create(): ProgramsStore =
        object : ProgramsStore,
            Store<ProgramsStore.Intent, ProgramsStore.State, ProgramsStore.Label> by storeFactory.create(
                name = "ProgramsStore",
                initialState = ProgramsStore.State(),
                bootstrapper = BootstrapperImpl(),
                executorFactory = ::ExecutorImpl,
                reducer = ReducerImpl
            ) {}

    private sealed interface Action {
        data class ProgramsLoaded(val programs: List<TrainingProgram>) : Action
        data class SelectedProgramLoaded(val programCode: String?) : Action
    }

    private sealed interface Msg {
        data class ProgramsLoaded(val programs: List<TrainingProgram>) : Msg
        data class SelectedProgramLoaded(val programCode: String?) : Msg
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            getAllProgramsUseCase()
                .flowOn(dispatcherProvider.io)
                .onEach { programs ->
                    dispatch(Action.ProgramsLoaded(programs))
                }
                .launchIn(scope)

            getSelectedProgramUseCase()
                .flowOn(dispatcherProvider.io)
                .onEach { program ->
                    dispatch(Action.SelectedProgramLoaded(program?.code))
                }
                .launchIn(scope)
        }
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<ProgramsStore.Intent, Action, ProgramsStore.State, Msg, ProgramsStore.Label>() {

        override fun executeAction(action: Action) {
            when (action) {
                is Action.ProgramsLoaded -> dispatch(Msg.ProgramsLoaded(action.programs))
                is Action.SelectedProgramLoaded -> dispatch(Msg.SelectedProgramLoaded(action.programCode))
            }
        }

        override fun executeIntent(intent: ProgramsStore.Intent) {
            when (intent) {
                is ProgramsStore.Intent.SelectProgram -> {
                    scope.launch {
                        withContext(dispatcherProvider.io) {
                            selectProgramUseCase(intent.programCode)
                        }
                    }
                }
            }
        }
    }

    private object ReducerImpl : Reducer<ProgramsStore.State, Msg> {
        override fun ProgramsStore.State.reduce(msg: Msg): ProgramsStore.State =
            when (msg) {
                is Msg.ProgramsLoaded -> copy(programs = msg.programs, isLoading = false)
                is Msg.SelectedProgramLoaded -> copy(selectedProgramCode = msg.programCode)
            }
    }
}
