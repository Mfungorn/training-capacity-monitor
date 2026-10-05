package com.fungorn.trainingcapacity.feature.mesocycles.presentation.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.fungorn.trainingcapacity.core.domain.model.GroupPriority
import com.fungorn.trainingcapacity.core.domain.model.TrainingGroup
import com.fungorn.trainingcapacity.core.domain.model.TrainingMesocycle
import com.fungorn.trainingcapacity.core.domain.usecase.ExportMesocycleStatisticsUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetAllProgramsUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetMesocycleStatisticsUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetSelectedProgramUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.StartNewMesocycleUseCase
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.model.RirRange
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.store.MesocycleDetailStore
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.store.MesocycleDetailStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DefaultMesocycleDetailComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    getMesocycleStatisticsUseCase: GetMesocycleStatisticsUseCase,
    getAllProgramsUseCase: GetAllProgramsUseCase,
    getSelectedProgramUseCase: GetSelectedProgramUseCase,
    startNewMesocycleUseCase: StartNewMesocycleUseCase,
    exportMesocycleStatisticsUseCase: ExportMesocycleStatisticsUseCase,
    private val mesocycleId: String?,
    private val onOutput: (MesocycleDetailComponent.Output) -> Unit
) : MesocycleDetailComponent, ComponentContext by componentContext {

    private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    private val store = instanceKeeper.getStore {
        MesocycleDetailStoreFactory(
            storeFactory = storeFactory,
            getMesocycleStatisticsUseCase = getMesocycleStatisticsUseCase,
            getAllProgramsUseCase = getAllProgramsUseCase,
            getSelectedProgramUseCase = getSelectedProgramUseCase,
            startNewMesocycleUseCase = startNewMesocycleUseCase,
            exportMesocycleStatisticsUseCase = exportMesocycleStatisticsUseCase
        ).create(mesocycleId)
    }

    init {
        lifecycle.doOnDestroy { scope.cancel() }

        store.labels
            .onEach { label ->
                when (label) {
                    is MesocycleDetailStore.Label.MesocycleSaved ->
                        onOutput(MesocycleDetailComponent.Output.NavigateBack)

                    is MesocycleDetailStore.Label.ShowError -> { /* Handle error */
                    }
                }
            }
            .launchIn(scope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val state: StateFlow<MesocycleDetailStore.State> = store.stateFlow

    override fun onStartDateChange(millis: Long) {
        store.accept(MesocycleDetailStore.Intent.UpdateStartDate(millis))
    }

    override fun onAddWeek() {
        store.accept(
            MesocycleDetailStore.Intent.AddWeek(
                type = TrainingMesocycle.Week.Type.WORKING,
                rirRange = RirRange.RIR_2_4
            )
        )
    }

    override fun onUpdateWeek(index: Int, type: TrainingMesocycle.Week.Type, rirRange: RirRange) {
        store.accept(MesocycleDetailStore.Intent.UpdateWeek(index, type, rirRange))
    }

    override fun onRemoveWeek(index: Int) {
        store.accept(MesocycleDetailStore.Intent.RemoveWeek(index))
    }

    override fun onProgramSelect(code: String) {
        store.accept(MesocycleDetailStore.Intent.SelectProgram(code))
    }

    override fun onGroupPriorityChange(group: TrainingGroup, priority: GroupPriority?) {
        store.accept(MesocycleDetailStore.Intent.SetGroupPriority(group, priority))
    }

    override fun onExportClick() {
        store.accept(MesocycleDetailStore.Intent.Export)
    }

    override fun onStartNewMesocycleClick() {
        onOutput(MesocycleDetailComponent.Output.NavigateToCreateMesocycle)
    }

    override fun onSaveClick() {
        store.accept(MesocycleDetailStore.Intent.Save)
    }

    override fun onCancelClick() {
        onOutput(MesocycleDetailComponent.Output.NavigateBack)
    }
}
