package com.fungorn.trainingcapacity.feature.form.presentation.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.fungorn.trainingcapacity.core.domain.model.TrainingEntry
import com.fungorn.trainingcapacity.core.domain.usecase.AddEntryUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetEntryByIdUseCase
import com.fungorn.trainingcapacity.feature.form.domain.usecase.GetCurrentTrainingContextUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class FormStoreFactory(
    private val storeFactory: StoreFactory,
    private val addEntryUseCase: AddEntryUseCase,
    private val getEntryByIdUseCase: GetEntryByIdUseCase,
    private val getCurrentTrainingContextUseCase: GetCurrentTrainingContextUseCase
) {

    fun create(entryId: String? = null): FormStore =
        object : FormStore,
            Store<FormStore.Intent, FormStore.State, FormStore.Label> by storeFactory.create(
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
            val maxCapacitySets: Int,
            val overallDifficulty: Int
        ) : Msg

        data class UpdateMaxCapacitySets(val value: Int) : Msg
        data class UpdateOverallDifficulty(val value: Int) : Msg
        data class SetErrorMessage(val error: String?) : Msg
    }

    private inner class ExecutorImpl(
        private val entryId: String?
    ) : CoroutineExecutor<FormStore.Intent, Nothing, FormStore.State, Msg, FormStore.Label>() {

        override fun executeAction(action: Nothing) {}

        override fun executeIntent(intent: FormStore.Intent) {
            when (intent) {
                is FormStore.Intent.LoadEntry -> loadEntry(intent.id)
                is FormStore.Intent.UpdateMaxCapacitySets -> dispatch(
                    Msg.UpdateMaxCapacitySets(
                        intent.value
                    )
                )

                is FormStore.Intent.UpdateOverallDifficulty -> dispatch(
                    Msg.UpdateOverallDifficulty(
                        intent.value
                    )
                )

                is FormStore.Intent.SaveEntry -> saveEntry()
            }
        }

        private fun loadEntry(id: String) {
            scope.launch {
                dispatch(Msg.StartLoading)
                try {
                    val entry = withContext(Dispatchers.IO) {
                        getEntryByIdUseCase(id).firstOrNull()
                    }
                    if (entry != null) {
                        dispatch(
                            Msg.EntryLoaded(
                                id = entry.id,
                                maxCapacitySets = entry.spentMaximumCapacitySets,
                                overallDifficulty = entry.overallDifficulty
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

        @OptIn(ExperimentalUuidApi::class)
        private fun saveEntry() {
            val currentState = state()
            scope.launch {
                dispatch(Msg.StartLoading)
                try {
                    val context = withContext(Dispatchers.IO) {
                        getCurrentTrainingContextUseCase().firstOrNull()
                    }
                    if (context == null) {
                        dispatch(Msg.StopLoading)
                        publish(FormStore.Label.ShowError("No active mesocycle"))
                        return@launch
                    }

                    val entry = TrainingEntry(
                        id = currentState.id ?: Uuid.random().toString(),
                        mesocycleId = context.mesocycleId,
                        programId = context.currentProgram.code,
                        weekNumber = context.currentWeekNumber,
                        trainingDayNumber = context.nextTrainingDayNumber,
                        spentMaximumCapacitySets = currentState.maxCapacitySets,
                        overallDifficulty = currentState.overallDifficulty,
                        createdAt = Clock.System.now().toEpochMilliseconds()
                    )
                    withContext(Dispatchers.IO) {
                        addEntryUseCase(entry)
                    }
                    dispatch(Msg.StopLoading)
                    publish(FormStore.Label.EntrySaved)
                } catch (e: Exception) {
                    dispatch(Msg.StopLoading)
                    publish(FormStore.Label.ShowError(e.message ?: "Failed to save entry"))
                }
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
                    maxCapacitySets = msg.maxCapacitySets,
                    overallDifficulty = msg.overallDifficulty,
                    isLoading = false
                )

                is Msg.UpdateMaxCapacitySets -> copy(maxCapacitySets = msg.value)
                is Msg.UpdateOverallDifficulty -> copy(overallDifficulty = msg.value)
                is Msg.SetErrorMessage -> copy(errorMessage = msg.error)
            }
    }
}
