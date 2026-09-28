package com.fungorn.trainingcapacity.feature.programs.presentation.store

import androidx.compose.runtime.snapshots.SnapshotStateSet
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.fungorn.trainingcapacity.core.common.DispatcherProvider
import com.fungorn.trainingcapacity.core.domain.model.TrainingDay
import com.fungorn.trainingcapacity.core.domain.model.TrainingProgram
import com.fungorn.trainingcapacity.core.domain.usecase.CreateProgramUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetProgramByCodeUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ProgramDetailStoreFactory(
    private val storeFactory: StoreFactory,
    private val dispatcherProvider: DispatcherProvider,
    private val getProgramByCodeUseCase: GetProgramByCodeUseCase,
    private val createProgramUseCase: CreateProgramUseCase,
    private val programCode: String?
) {
    fun create(): ProgramDetailStore =
        object : ProgramDetailStore, Store<ProgramDetailStore.Intent, ProgramDetailStore.State, ProgramDetailStore.Label> by storeFactory.create(
            name = "ProgramDetailStore",
            initialState = ProgramDetailStore.State(),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data class ProgramLoaded(val state: ProgramDetailStore.State) : Action
    }

    private sealed interface Msg {
        data class LoadProgram(val state: ProgramDetailStore.State) : Msg
        data class UpdateName(val name: String) : Msg
        data class ToggleExpanded(val dayIndex: Int) : Msg
        data object StartSaving : Msg
        data object SaveSuccess : Msg
        data class SaveError(val error: String) : Msg
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            programCode?.let { code ->
                scope.launch {
                    val program = getProgramByCodeUseCase(code)
                        .flowOn(dispatcherProvider.io)
                        .first()
                    program?.let {
                        dispatch(Action.ProgramLoaded(it.toDetailState()))
                    }
                }
            }
        }
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<ProgramDetailStore.Intent, Action, ProgramDetailStore.State, Msg, ProgramDetailStore.Label>() {

        override fun executeAction(action: Action) {
            when (action) {
                is Action.ProgramLoaded -> dispatch(Msg.LoadProgram(action.state))
            }
        }

        @OptIn(ExperimentalUuidApi::class)
        override fun executeIntent(intent: ProgramDetailStore.Intent) {
            when (intent) {
                is ProgramDetailStore.Intent.UpdateProgramName -> {
                    dispatch(Msg.UpdateName(intent.name))
                }

                is ProgramDetailStore.Intent.AddTrainingDay -> {
                    val currentDays = state().trainingDays
                    val newDay = ProgramDetailStore.TrainingDayState(
                        name = "Day #${currentDays.size + 1}",
                        muscleGroups = SnapshotStateSet(),
                        isExpanded = true
                    )
                    currentDays.add(newDay)
                }

                is ProgramDetailStore.Intent.RemoveTrainingDay -> {
                    val currentDays = state().trainingDays
                    if (currentDays.size > 1) {
                        currentDays.removeAt(intent.index)
                        currentDays.forEachIndexed { index, day ->
                            currentDays[index] = day.copy(name = "Day #${index + 1}")
                        }
                    }
                }

                is ProgramDetailStore.Intent.ToggleMuscleGroup -> {
                    val day = state().trainingDays[intent.dayIndex]
                    if (intent.group in day.muscleGroups) {
                        day.muscleGroups.remove(intent.group)
                    } else {
                        day.muscleGroups.add(intent.group)
                    }
                }

                is ProgramDetailStore.Intent.ToggleDayExpanded -> {
                    dispatch(Msg.ToggleExpanded(intent.dayIndex))
                }

                is ProgramDetailStore.Intent.Save -> {
                    scope.launch {
                        dispatch(Msg.StartSaving)
                        try {
                            val state = state()
                            val program = TrainingProgram(
                                code = programCode ?: Uuid.random().toString(),
                                name = state.programName,
                                trainingDays = state.trainingDays.mapIndexed { index, dayState ->
                                    TrainingDay(
                                        id = "${programCode ?: "NEW"}_D${index + 1}",
                                        name = dayState.name,
                                        muscleGroups = dayState.muscleGroups.toSet()
                                    )
                                }
                            )
                            withContext(dispatcherProvider.io) {
                                createProgramUseCase(program)
                            }
                            dispatch(Msg.SaveSuccess)
                            publish(ProgramDetailStore.Label.NavigateBack)
                        } catch (e: Exception) {
                            dispatch(Msg.SaveError(e.message ?: "Unknown error"))
                        }
                    }
                }
            }
        }
    }

    private object ReducerImpl : Reducer<ProgramDetailStore.State, Msg> {
        override fun ProgramDetailStore.State.reduce(msg: Msg): ProgramDetailStore.State =
            when (msg) {
                is Msg.LoadProgram -> msg.state
                is Msg.UpdateName -> copy(programName = msg.name)
                is Msg.ToggleExpanded -> {
                    val day = trainingDays[msg.dayIndex]
                    trainingDays[msg.dayIndex] = day.copy(isExpanded = !day.isExpanded)
                    this
                }
                is Msg.StartSaving -> copy(isSaving = true, error = null)
                is Msg.SaveSuccess -> copy(isSaving = false)
                is Msg.SaveError -> copy(isSaving = false, error = msg.error)
            }
    }
}
