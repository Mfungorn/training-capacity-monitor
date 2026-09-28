package com.fungorn.trainingcapacity.feature.programs.presentation.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.fungorn.trainingcapacity.core.common.DispatcherProvider
import com.fungorn.trainingcapacity.core.domain.model.TrainingGroup
import com.fungorn.trainingcapacity.core.domain.usecase.CreateProgramUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetProgramByCodeUseCase
import com.fungorn.trainingcapacity.feature.programs.presentation.store.ProgramDetailStore
import com.fungorn.trainingcapacity.feature.programs.presentation.store.ProgramDetailStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DefaultProgramDetailComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    dispatcherProvider: DispatcherProvider,
    getProgramByCodeUseCase: GetProgramByCodeUseCase,
    createProgramUseCase: CreateProgramUseCase,
    programCode: String?,
    private val onOutput: (ProgramDetailComponent.Output) -> Unit
) : ProgramDetailComponent, ComponentContext by componentContext {

    private val scope = CoroutineScope(dispatcherProvider.main + SupervisorJob())

    private val store = instanceKeeper.getStore {
        ProgramDetailStoreFactory(
            storeFactory = storeFactory,
            dispatcherProvider = dispatcherProvider,
            getProgramByCodeUseCase = getProgramByCodeUseCase,
            createProgramUseCase = createProgramUseCase,
            programCode = programCode
        ).create()
    }

    init {
        store.labels
            .onEach { label ->
                when (label) {
                    ProgramDetailStore.Label.NavigateBack -> 
                        onOutput(ProgramDetailComponent.Output.NavigateBack)
                }
            }
            .launchIn(scope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val state: StateFlow<ProgramDetailStore.State> = store.stateFlow

    override fun onProgramNameChange(name: String) {
        store.accept(ProgramDetailStore.Intent.UpdateProgramName(name))
    }

    override fun onAddTrainingDay() {
        store.accept(ProgramDetailStore.Intent.AddTrainingDay)
    }

    override fun onRemoveTrainingDay(index: Int) {
        store.accept(ProgramDetailStore.Intent.RemoveTrainingDay(index))
    }

    override fun onToggleMuscleGroup(dayIndex: Int, group: TrainingGroup) {
        store.accept(ProgramDetailStore.Intent.ToggleMuscleGroup(dayIndex, group))
    }

    override fun onToggleDayExpanded(dayIndex: Int) {
        store.accept(ProgramDetailStore.Intent.ToggleDayExpanded(dayIndex))
    }

    override fun onSaveClick() {
        store.accept(ProgramDetailStore.Intent.Save)
    }

    override fun onCancelClick() {
        onOutput(ProgramDetailComponent.Output.NavigateBack)
    }
}
