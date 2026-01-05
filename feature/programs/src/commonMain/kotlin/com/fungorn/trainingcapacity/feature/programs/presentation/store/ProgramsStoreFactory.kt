package com.fungorn.trainingcapacity.feature.programs.presentation.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.fungorn.trainingcapacity.core.domain.model.TrainingProgram

class ProgramsStoreFactory(
    private val storeFactory: StoreFactory
) {

    fun create(): ProgramsStore =
        object : ProgramsStore,
            Store<ProgramsStore.Intent, ProgramsStore.State, ProgramsStore.Label> by storeFactory.create(
                name = "ProgramsStore",
                initialState = ProgramsStore.State(),
                executorFactory = ::ExecutorImpl,
                reducer = ReducerImpl
            ) {}

    private sealed interface Msg {
        data class SelectProgram(val program: TrainingProgram) : Msg
    }

    private class ExecutorImpl :
        CoroutineExecutor<ProgramsStore.Intent, Nothing, ProgramsStore.State, Msg, ProgramsStore.Label>() {

        override fun executeAction(action: Nothing) {}

        override fun executeIntent(intent: ProgramsStore.Intent) {
            when (intent) {
                is ProgramsStore.Intent.SelectProgram -> {
                    dispatch(Msg.SelectProgram(intent.program))
                }
            }
        }
    }

    private object ReducerImpl : Reducer<ProgramsStore.State, Msg> {
        override fun ProgramsStore.State.reduce(msg: Msg): ProgramsStore.State =
            when (msg) {
                is Msg.SelectProgram -> copy(selectedProgram = msg.program)
            }
    }
}
