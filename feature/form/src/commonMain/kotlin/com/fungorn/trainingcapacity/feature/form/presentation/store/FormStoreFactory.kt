package com.fungorn.trainingcapacity.feature.form.presentation.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.fungorn.trainingcapacity.core.domain.model.TrainingEntry
import com.fungorn.trainingcapacity.core.domain.model.TrainingGroup
import com.fungorn.trainingcapacity.core.domain.model.TrainingProgram
import com.fungorn.trainingcapacity.core.domain.usecase.AddEntryUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetAllProgramsUseCase
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
    private val getAllProgramsUseCase: GetAllProgramsUseCase,
    private val getCurrentTrainingContextUseCase: GetCurrentTrainingContextUseCase
) {

    fun create(entryId: String? = null): FormStore =
        object : FormStore,
            Store<FormStore.Intent, FormStore.State, FormStore.Label> by storeFactory.create(
                name = "FormStore",
                initialState = FormStore.State(),
                bootstrapper = BootstrapperImpl(entryId),
                executorFactory = ::ExecutorImpl,
                reducer = ReducerImpl
            ) {}

    private sealed interface Action {
        data class SessionLoaded(
            val programs: List<TrainingProgram>,
            val programCode: String?,
            val weekNumber: Int?,
            val trainingDayNumber: Int
        ) : Action
    }

    private sealed interface Msg {
        data object StartLoading : Msg
        data object StopLoading : Msg
        data class SessionLoaded(
            val programs: List<TrainingProgram>,
            val programCode: String?,
            val weekNumber: Int?,
            val trainingDayNumber: Int
        ) : Msg

        data class EntryLoaded(val entry: TrainingEntry) : Msg
        data class SelectProgram(val code: String) : Msg
        data class SelectTrainingDay(val number: Int) : Msg
        data class UpdateCompleted(val isCompleted: Boolean) : Msg
        data class UpdateMaxCapacitySets(val value: Int) : Msg
        data class UpdateOverallDifficulty(val value: Int) : Msg
        data class UpdateDuration(val minutes: Int) : Msg
        data class UpdateReadiness(val value: Int) : Msg
        data class UpdateFatigue(val value: Int) : Msg
        data class UpdateMuscleGroupFatigue(val group: TrainingGroup, val value: Int) : Msg
        data class SetErrorMessage(val error: String?) : Msg
    }

    private inner class BootstrapperImpl(
        private val entryId: String?
    ) : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            if (entryId != null) return
            scope.launch {
                val (programs, context) = withContext(Dispatchers.IO) {
                    getAllProgramsUseCase().firstOrNull()
                        .orEmpty() to getCurrentTrainingContextUseCase().firstOrNull()
                }
                dispatch(
                    Action.SessionLoaded(
                        programs = programs,
                        programCode = context?.currentProgram?.code,
                        weekNumber = context?.currentWeekNumber,
                        trainingDayNumber = context?.nextTrainingDayNumber ?: 1
                    )
                )
            }
        }
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<FormStore.Intent, Action, FormStore.State, Msg, FormStore.Label>() {

        override fun executeAction(action: Action) {
            when (action) {
                is Action.SessionLoaded -> dispatch(
                    Msg.SessionLoaded(
                        action.programs,
                        action.programCode,
                        action.weekNumber,
                        action.trainingDayNumber
                    )
                )
            }
        }

        override fun executeIntent(intent: FormStore.Intent) {
            when (intent) {
                is FormStore.Intent.LoadEntry -> loadEntry(intent.id)
                is FormStore.Intent.SelectProgram -> dispatch(Msg.SelectProgram(intent.code))
                is FormStore.Intent.SelectTrainingDay -> dispatch(Msg.SelectTrainingDay(intent.number))
                is FormStore.Intent.UpdateCompleted -> dispatch(Msg.UpdateCompleted(intent.isCompleted))
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

                is FormStore.Intent.UpdateDuration -> dispatch(Msg.UpdateDuration(intent.minutes))
                is FormStore.Intent.UpdateReadiness -> dispatch(Msg.UpdateReadiness(intent.value))
                is FormStore.Intent.UpdateFatigue -> dispatch(Msg.UpdateFatigue(intent.value))
                is FormStore.Intent.UpdateMuscleGroupFatigue ->
                    dispatch(Msg.UpdateMuscleGroupFatigue(intent.group, intent.value))

                is FormStore.Intent.SaveEntry -> saveEntry()
            }
        }

        private fun loadEntry(id: String) {
            scope.launch {
                dispatch(Msg.StartLoading)
                try {
                    val (entry, programs) = withContext(Dispatchers.IO) {
                        getEntryByIdUseCase(id).firstOrNull() to getAllProgramsUseCase().firstOrNull()
                            .orEmpty()
                    }
                    if (entry != null) {
                        dispatch(
                            Msg.SessionLoaded(
                                programs,
                                entry.programId,
                                entry.weekNumber,
                                entry.trainingDayNumber
                            )
                        )
                        dispatch(Msg.EntryLoaded(entry))
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
            val program = currentState.selectedProgram
            if (program == null) {
                publish(FormStore.Label.ShowError("No program selected"))
                return
            }
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

                    val isCompleted = currentState.isCompleted
                    val dayGroups = currentState.trainingDayGroups.toSet()
                    val entry = TrainingEntry(
                        id = currentState.id ?: Uuid.random().toString(),
                        mesocycleId = context.mesocycleId,
                        programId = program.code,
                        weekNumber = currentState.weekNumber ?: context.currentWeekNumber,
                        trainingDayNumber = currentState.trainingDayNumber,
                        spentMaximumCapacitySets = if (isCompleted) currentState.maxCapacitySets else 0,
                        overallDifficulty = if (isCompleted) currentState.overallDifficulty else 0,
                        createdAt = Clock.System.now().toEpochMilliseconds(),
                        isCompleted = isCompleted,
                        durationMinutes = if (isCompleted) currentState.durationMinutes else 0,
                        readiness = currentState.readiness.takeIf { isCompleted },
                        fatigue = currentState.fatigue.takeIf { isCompleted },
                        muscleGroupFatigue = if (isCompleted) {
                            currentState.muscleGroupFatigue.filterKeys { it in dayGroups }
                        } else {
                            emptyMap()
                        },
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
                is Msg.SessionLoaded -> copy(
                    programs = msg.programs,
                    selectedProgramCode = msg.programCode ?: msg.programs.firstOrNull()?.code,
                    weekNumber = msg.weekNumber,
                    trainingDayNumber = msg.trainingDayNumber
                )

                is Msg.EntryLoaded -> copy(
                    id = msg.entry.id,
                    isCompleted = msg.entry.isCompleted,
                    maxCapacitySets = msg.entry.spentMaximumCapacitySets,
                    overallDifficulty = msg.entry.overallDifficulty,
                    durationMinutes = msg.entry.durationMinutes,
                    readiness = msg.entry.readiness,
                    fatigue = msg.entry.fatigue,
                    muscleGroupFatigue = msg.entry.muscleGroupFatigue,
                    isLoading = false
                )

                is Msg.SelectProgram -> copy(selectedProgramCode = msg.code, trainingDayNumber = 1)
                is Msg.SelectTrainingDay -> copy(trainingDayNumber = msg.number)
                is Msg.UpdateCompleted -> copy(isCompleted = msg.isCompleted)
                is Msg.UpdateMaxCapacitySets -> copy(maxCapacitySets = msg.value)
                is Msg.UpdateOverallDifficulty -> copy(overallDifficulty = msg.value)
                is Msg.UpdateDuration -> copy(durationMinutes = msg.minutes)
                is Msg.UpdateReadiness -> copy(readiness = msg.value)
                is Msg.UpdateFatigue -> copy(fatigue = msg.value)
                is Msg.UpdateMuscleGroupFatigue ->
                    copy(muscleGroupFatigue = muscleGroupFatigue + (msg.group to msg.value))

                is Msg.SetErrorMessage -> copy(errorMessage = msg.error)
            }
    }
}
