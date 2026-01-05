package com.fungorn.trainingcapacity.feature.mesocycles.presentation.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.fungorn.trainingcapacity.core.domain.model.TrainingMesocycle
import com.fungorn.trainingcapacity.core.domain.model.TrainingProgram
import com.fungorn.trainingcapacity.core.domain.repository.MesocycleRepository
import com.fungorn.trainingcapacity.core.domain.usecase.StartNewMesocycleUseCase
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.model.Mode
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.model.RirRange
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.model.WeekState
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class MesocycleDetailStoreFactory(
    private val storeFactory: StoreFactory,
    private val mesocycleRepository: MesocycleRepository,
    private val startNewMesocycleUseCase: StartNewMesocycleUseCase
) {

    fun create(mesocycleId: String? = null): MesocycleDetailStore =
        object : MesocycleDetailStore,
            Store<MesocycleDetailStore.Intent, MesocycleDetailStore.State, MesocycleDetailStore.Label> by storeFactory.create(
                name = "MesocycleDetailStore",
                initialState = MesocycleDetailStore.State(
                    mode = if (mesocycleId == null) Mode.CREATE else Mode.VIEW
                ),
                bootstrapper = BootstrapperImpl(mesocycleId),
                executorFactory = ::ExecutorImpl,
                reducer = ReducerImpl
            ) {}

    private sealed interface Action {
        data class MesocycleLoaded(
            val startDateMillis: Long,
            val weeks: List<WeekState>
        ) : Action

        data class LoadError(val message: String) : Action
    }

    private sealed interface Msg {
        data object StartLoading : Msg
        data object StopLoading : Msg
        data class MesocycleLoaded(
            val startDateMillis: Long,
            val weeks: List<WeekState>
        ) : Msg

        data class UpdateStartDate(val millis: Long) : Msg
        data class SetWeeks(val weeks: List<WeekState>) : Msg
        data class SetError(val message: String?) : Msg
    }

    private inner class BootstrapperImpl(
        private val mesocycleId: String?
    ) : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            if (mesocycleId != null) {
                scope.launch {
                    try {
                        val mesocycle = mesocycleRepository.getCycleById(mesocycleId).firstOrNull()
                        if (mesocycle != null) {
                            val weeks = mesocycle.weeks.map { week ->
                                WeekState(
                                    type = week.type,
                                    rirRange = RirRange.entries.find {
                                        it.lowerEnd == week.maximumCapacityLowerEnd &&
                                                it.upperEnd == week.maximumCapacityUpperEnd
                                    } ?: RirRange.RIR_2_4
                                )
                            }
                            dispatch(Action.MesocycleLoaded(mesocycle.startedAt, weeks))
                        } else {
                            dispatch(Action.LoadError("Mesocycle not found"))
                        }
                    } catch (e: Exception) {
                        dispatch(Action.LoadError(e.message ?: "Failed to load mesocycle"))
                    }
                }
            }
        }
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<MesocycleDetailStore.Intent, Action, MesocycleDetailStore.State, Msg, MesocycleDetailStore.Label>() {

        override fun executeAction(action: Action) {
            when (action) {
                is Action.MesocycleLoaded -> dispatch(
                    Msg.MesocycleLoaded(
                        action.startDateMillis,
                        action.weeks
                    )
                )

                is Action.LoadError -> {
                    dispatch(Msg.SetError(action.message))
                    publish(MesocycleDetailStore.Label.ShowError(action.message))
                }
            }
        }

        override fun executeIntent(intent: MesocycleDetailStore.Intent) {
            when (intent) {
                is MesocycleDetailStore.Intent.UpdateStartDate -> dispatch(
                    Msg.UpdateStartDate(
                        intent.millis
                    )
                )

                is MesocycleDetailStore.Intent.AddWeek -> {
                    val currentWeeks = state().weeks.toMutableList()
                    currentWeeks.add(WeekState(intent.type, intent.rirRange))
                    dispatch(Msg.SetWeeks(currentWeeks))
                }

                is MesocycleDetailStore.Intent.UpdateWeek -> {
                    val currentWeeks = state().weeks.toMutableList()
                    if (intent.index in currentWeeks.indices) {
                        currentWeeks[intent.index] = WeekState(intent.type, intent.rirRange)
                        dispatch(Msg.SetWeeks(currentWeeks))
                    }
                }

                is MesocycleDetailStore.Intent.RemoveWeek -> {
                    val currentWeeks = state().weeks.toMutableList()
                    if (intent.index in currentWeeks.indices && currentWeeks.size > 1) {
                        currentWeeks.removeAt(intent.index)
                        dispatch(Msg.SetWeeks(currentWeeks))
                    }
                }

                is MesocycleDetailStore.Intent.Save -> saveMesocycle()
            }
        }

        @OptIn(ExperimentalUuidApi::class)
        private fun saveMesocycle() {
            val currentState = state()
            if (!currentState.isCreateMode) return

            scope.launch {
                dispatch(Msg.StartLoading)
                try {
                    val mesocycle = TrainingMesocycle(
                        id = Uuid.random().toString(),
                        program = TrainingProgram.UpperLower,
                        weeks = currentState.weeks.map { weekState ->
                            TrainingMesocycle.Week(
                                type = weekState.type,
                                maximumCapacityLowerEnd = weekState.rirRange.lowerEnd,
                                maximumCapacityUpperEnd = weekState.rirRange.upperEnd
                            )
                        },
                        isSelected = true,
                        startedAt = currentState.startDateMillis
                    )
                    startNewMesocycleUseCase(mesocycle)
                    dispatch(Msg.StopLoading)
                    publish(MesocycleDetailStore.Label.MesocycleSaved)
                } catch (e: Exception) {
                    dispatch(Msg.StopLoading)
                    dispatch(Msg.SetError(e.message))
                    publish(
                        MesocycleDetailStore.Label.ShowError(
                            e.message ?: "Failed to save mesocycle"
                        )
                    )
                }
            }
        }
    }

    private object ReducerImpl : Reducer<MesocycleDetailStore.State, Msg> {
        override fun MesocycleDetailStore.State.reduce(msg: Msg): MesocycleDetailStore.State =
            when (msg) {
                is Msg.StartLoading -> copy(isLoading = true)
                is Msg.StopLoading -> copy(isLoading = false)
                is Msg.MesocycleLoaded -> copy(
                    startDateMillis = msg.startDateMillis,
                    weeks = weeks.apply {
                        clear()
                        addAll(msg.weeks)
                    },
                    isLoading = false
                )

                is Msg.UpdateStartDate -> copy(startDateMillis = msg.millis)
                is Msg.SetWeeks -> copy(
                    weeks = weeks.apply {
                        clear()
                        addAll(msg.weeks)
                    }
                )

                is Msg.SetError -> copy(errorMessage = msg.message)
            }
    }
}
