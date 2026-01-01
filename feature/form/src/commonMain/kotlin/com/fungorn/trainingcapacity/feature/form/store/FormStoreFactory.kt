package com.fungorn.trainingcapacity.feature.form.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.fungorn.trainingcapacity.core.domain.repository.TrainingRepository
import com.fungorn.trainingcapacity.core.domain.usecase.AddEntryUseCase
import com.fungorn.trainingcapacity.feature.form.store.FormStore.Intent
import com.fungorn.trainingcapacity.feature.form.store.FormStore.Label
import com.fungorn.trainingcapacity.feature.form.store.FormStore.State
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class FormStoreFactory(
    private val storeFactory: StoreFactory,
    private val addEntryUseCase: AddEntryUseCase,
    private val repository: TrainingRepository
) {

    fun create(entryId: String? = null): FormStore =
        object : FormStore, Store<Intent, State, Label> by storeFactory.create(
            name = "FormStore",
            initialState = State(),
            executorFactory = { ExecutorImpl(entryId) },
            reducer = ReducerImpl
        ) {}

    private sealed interface Msg {
        data object StartLoading : Msg
        data object StopLoading : Msg
        data class EntryLoaded(
            val id: String,
            // TODO
        ) : Msg

        // TODO

        data class SetErrorMessage(val error: String?) : Msg
    }

    private inner class ExecutorImpl(
        private val entryId: String?
    ) : CoroutineExecutor<Intent, Nothing, State, Msg, Label>() {

        override fun executeAction(action: Nothing) {}

        override fun executeIntent(intent: Intent) {
            when (intent) {
                is Intent.LoadEntry -> loadEntry(intent.id)
                // TODO
                is Intent.SaveEntry -> saveEntry()
            }
        }

        private fun loadEntry(id: String) {
            scope.launch {
                dispatch(Msg.StartLoading)
                try {
                    val entry = repository.getEntryById(id).firstOrNull()
                    if (entry != null) {
                        dispatch(
                            Msg.EntryLoaded(
                                id = entry.id,
                                // TODO
                            )
                        )
                    } else {
                        dispatch(Msg.StopLoading)
                        publish(Label.ShowError("Entry not found"))
                    }
                } catch (e: Exception) {
                    dispatch(Msg.StopLoading)
                    publish(Label.ShowError(e.message ?: "Failed to load entry"))
                }
            }
        }

        private fun saveEntry() {
            val currentState = state()
            // TODO
            scope.launch {
                dispatch(Msg.StartLoading)
                // TODO
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.StartLoading -> copy(isLoading = true)
                is Msg.StopLoading -> copy(isLoading = false)
                is Msg.EntryLoaded -> copy(
                    id = msg.id,
                    isLoading = false,
                )
                // TODO
                is Msg.SetErrorMessage -> copy(errorMessage = msg.error)
            }
    }
}
