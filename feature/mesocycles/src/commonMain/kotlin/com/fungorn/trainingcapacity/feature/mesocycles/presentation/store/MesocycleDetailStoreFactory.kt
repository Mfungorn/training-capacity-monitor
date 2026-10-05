package com.fungorn.trainingcapacity.feature.mesocycles.presentation.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.fungorn.trainingcapacity.core.domain.model.GroupPriority
import com.fungorn.trainingcapacity.core.domain.model.MesocycleStatistics
import com.fungorn.trainingcapacity.core.domain.model.TrainingGroup
import com.fungorn.trainingcapacity.core.domain.model.TrainingMesocycle
import com.fungorn.trainingcapacity.core.domain.model.TrainingProgram
import com.fungorn.trainingcapacity.core.domain.usecase.ExportMesocycleStatisticsUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetAllProgramsUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetMesocycleStatisticsUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetSelectedProgramUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.StartNewMesocycleUseCase
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.model.Mode
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.model.RirRange
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.model.WeekState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class MesocycleDetailStoreFactory(
    private val storeFactory: StoreFactory,
    private val getMesocycleStatisticsUseCase: GetMesocycleStatisticsUseCase,
    private val getAllProgramsUseCase: GetAllProgramsUseCase,
    private val getSelectedProgramUseCase: GetSelectedProgramUseCase,
    private val startNewMesocycleUseCase: StartNewMesocycleUseCase,
    private val exportMesocycleStatisticsUseCase: ExportMesocycleStatisticsUseCase
) {

    fun create(mesocycleId: String? = null): MesocycleDetailStore =
        object : MesocycleDetailStore,
            Store<MesocycleDetailStore.Intent, MesocycleDetailStore.State, MesocycleDetailStore.Label> by storeFactory.create(
                name = "MesocycleDetailStore",
                initialState = MesocycleDetailStore.State(
                    mode = if (mesocycleId == null) Mode.CREATE else Mode.VIEW
                ),
                bootstrapper = BootstrapperImpl(mesocycleId),
                executorFactory = { ExecutorImpl(mesocycleId) },
                reducer = ReducerImpl
            ) {}

    private sealed interface Action {
        data class MesocycleLoaded(val statistics: MesocycleStatistics) : Action
        data class ProgramsLoaded(val programs: List<TrainingProgram>, val selectedCode: String?) :
            Action
        data class LoadError(val message: String) : Action
    }

    private sealed interface Msg {
        data object StartLoading : Msg
        data object StopLoading : Msg
        data class MesocycleLoaded(val statistics: MesocycleStatistics) : Msg
        data class UpdateStartDate(val millis: Long) : Msg
        data class SetWeeks(val weeks: List<WeekState>) : Msg
        data class SetPrograms(val programs: List<TrainingProgram>, val selectedCode: String?) : Msg
        data class SelectProgram(val code: String) : Msg
        data class SetGroupPriorities(val priorities: Map<TrainingGroup, GroupPriority>) : Msg
        data class SetExporting(val isExporting: Boolean) : Msg
        data class SetError(val message: String?) : Msg
    }

    private inner class BootstrapperImpl(
        private val mesocycleId: String?
    ) : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            if (mesocycleId == null) {
                scope.launch {
                    try {
                        val (programs, selected) = withContext(Dispatchers.IO) {
                            getAllProgramsUseCase().firstOrNull()
                                .orEmpty() to getSelectedProgramUseCase().firstOrNull()
                        }
                        dispatch(
                            Action.ProgramsLoaded(
                                programs,
                                selected?.code ?: programs.firstOrNull()?.code
                            )
                        )
                    } catch (e: Exception) {
                        dispatch(Action.LoadError(e.message ?: "Failed to load programs"))
                    }
                }
            } else {
                scope.launch {
                    try {
                        val statistics = withContext(Dispatchers.IO) {
                            getMesocycleStatisticsUseCase(mesocycleId).firstOrNull()
                        }
                        if (statistics != null) {
                            dispatch(Action.MesocycleLoaded(statistics))
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

    private inner class ExecutorImpl(
        private val mesocycleId: String?
    ) : CoroutineExecutor<MesocycleDetailStore.Intent, Action, MesocycleDetailStore.State, Msg, MesocycleDetailStore.Label>() {

        override fun executeAction(action: Action) {
            when (action) {
                is Action.MesocycleLoaded -> dispatch(Msg.MesocycleLoaded(action.statistics))
                is Action.ProgramsLoaded -> dispatch(
                    Msg.SetPrograms(
                        action.programs,
                        action.selectedCode
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
                    if (intent.index in currentWeeks.indices) {
                        currentWeeks.removeAt(intent.index)
                        dispatch(Msg.SetWeeks(currentWeeks))
                    }
                }

                is MesocycleDetailStore.Intent.SelectProgram -> dispatch(Msg.SelectProgram(intent.code))
                is MesocycleDetailStore.Intent.SetGroupPriority -> setGroupPriority(
                    intent.group,
                    intent.priority
                )
                is MesocycleDetailStore.Intent.Save -> saveMesocycle()
                is MesocycleDetailStore.Intent.Export -> exportStatistics()
            }
        }

        private fun setGroupPriority(group: TrainingGroup, priority: GroupPriority?) {
            val current = state()
            if (!current.isCreateMode) return
            val isNewFocus =
                priority == GroupPriority.FOCUS && current.groupPriorities[group] != GroupPriority.FOCUS
            if (isNewFocus && !current.canAddFocusGroup) {
                publish(
                    MesocycleDetailStore.Label.ShowError(
                        "Only ${TrainingMesocycle.MAX_FOCUS_GROUPS} focus groups are allowed"
                    )
                )
                return
            }
            val updated = if (priority == null) {
                current.groupPriorities - group
            } else {
                current.groupPriorities + (group to priority)
            }
            dispatch(Msg.SetGroupPriorities(updated))
        }

        @OptIn(ExperimentalUuidApi::class)
        private fun saveMesocycle() {
            val currentState = state()
            if (!currentState.isCreateMode) return
            val program = currentState.selectedProgram
            if (program == null) {
                publish(MesocycleDetailStore.Label.ShowError("No program selected"))
                return
            }
            if (currentState.weeks.isEmpty()) {
                publish(MesocycleDetailStore.Label.ShowError("Add at least one week"))
                return
            }

            scope.launch {
                dispatch(Msg.StartLoading)
                try {
                    val mesocycle = TrainingMesocycle(
                        id = Uuid.random().toString(),
                        program = program,
                        weeks = currentState.weeks.map { weekState ->
                            TrainingMesocycle.Week(
                                type = weekState.type,
                                maximumCapacityLowerEnd = weekState.rirRange.lowerEnd,
                                maximumCapacityUpperEnd = weekState.rirRange.upperEnd
                            )
                        },
                        isSelected = true,
                        startedAt = currentState.startDateMillis,
                        groupPriorities = currentState.groupPriorities
                    )
                    withContext(Dispatchers.IO) {
                        startNewMesocycleUseCase(mesocycle)
                    }
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

        private fun exportStatistics() {
            val id = mesocycleId ?: return
            if (state().isExporting) return
            scope.launch {
                dispatch(Msg.SetExporting(true))
                try {
                    withContext(Dispatchers.IO) {
                        exportMesocycleStatisticsUseCase(id)
                    }
                } catch (e: Exception) {
                    publish(
                        MesocycleDetailStore.Label.ShowError(
                            e.message ?: "Failed to export statistics"
                        )
                    )
                } finally {
                    dispatch(Msg.SetExporting(false))
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
                    startDateMillis = msg.statistics.mesocycle.startedAt,
                    weeks = weeks.apply {
                        clear()
                        addAll(msg.statistics.mesocycle.weeks.map(::toWeekState))
                    },
                    groupPriorities = msg.statistics.mesocycle.groupPriorities,
                    programs = listOf(msg.statistics.mesocycle.program),
                    selectedProgramCode = msg.statistics.mesocycle.program.code,
                    statistics = msg.statistics,
                    isLoading = false
                )

                is Msg.UpdateStartDate -> copy(startDateMillis = msg.millis)
                is Msg.SetWeeks -> copy(
                    weeks = weeks.apply {
                        clear()
                        addAll(msg.weeks)
                    }
                )

                is Msg.SetPrograms -> copy(
                    programs = msg.programs,
                    selectedProgramCode = msg.selectedCode
                )

                is Msg.SelectProgram -> copy(selectedProgramCode = msg.code)
                is Msg.SetGroupPriorities -> copy(groupPriorities = msg.priorities)
                is Msg.SetExporting -> copy(isExporting = msg.isExporting)
                is Msg.SetError -> copy(errorMessage = msg.message)
            }

        private fun toWeekState(week: TrainingMesocycle.Week) = WeekState(
            type = week.type,
            rirRange = RirRange.entries.find {
                it.lowerEnd == week.maximumCapacityLowerEnd &&
                        it.upperEnd == week.maximumCapacityUpperEnd
            } ?: RirRange.RIR_2_4
        )
    }
}
