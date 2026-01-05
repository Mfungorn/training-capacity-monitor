package com.fungorn.trainingcapacity.feature.form.presentation.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.fungorn.trainingcapacity.core.domain.usecase.AddEntryUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetEntryByIdUseCase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class FormStoreFactory(
    private val storeFactory: StoreFactory,
    private val addEntryUseCase: AddEntryUseCase,
    private val getEntryByIdUseCase: GetEntryByIdUseCase
) {

    fun create(entryId: String? = null): FormStore =
        object : FormStore, Store<FormStore.Intent, FormStore.State, FormStore.Label> by storeFactory.create(
            name = "FormStore",
            initialState = FormStore.State(),
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
    ) : CoroutineExecutor<FormStore.Intent, Nothing, FormStore.State, Msg, FormStore.Label>() {

        override fun executeAction(action: Nothing) {}

        override fun executeIntent(intent: FormStore.Intent) {
            when (intent) {
                is FormStore.Intent.LoadEntry -> loadEntry(intent.id)
                // TODO
                is FormStore.Intent.SaveEntry -> saveEntry()
            }
        }

        private fun loadEntry(id: String) {
            scope.launch {
                dispatch(Msg.StartLoading)
                try {
                    val entry = getEntryByIdUseCase(id).firstOrNull()
                    if (entry != null) {
                        dispatch(
                            Msg.EntryLoaded(
                                id = entry.id,
                                // TODO
                            )
                        )
                    } else {
                        dispatch(Msg.StopLoading)
                        publish(FormStore.Label.ShowError("Entry not found"))
                    }
                } catch (e: Exception) {
                    dispatch(Msg.StopLoading)
                    publish(FormStore.Label.ShowError(e.message ?: "Failed to load entry"))
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

    private object ReducerImpl : Reducer<FormStore.State, Msg> {
        override fun FormStore.State.reduce(msg: Msg): FormStore.State =
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
