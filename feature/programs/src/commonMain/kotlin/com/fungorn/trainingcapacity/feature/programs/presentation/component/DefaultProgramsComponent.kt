package com.fungorn.trainingcapacity.feature.programs.presentation.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.fungorn.trainingcapacity.core.common.DispatcherProvider
import com.fungorn.trainingcapacity.core.domain.model.TrainingProgram
import com.fungorn.trainingcapacity.core.domain.usecase.GetAllProgramsUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetSelectedProgramUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.SelectProgramUseCase
import com.fungorn.trainingcapacity.feature.programs.presentation.store.ProgramsStore
import com.fungorn.trainingcapacity.feature.programs.presentation.store.ProgramsStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class DefaultProgramsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    dispatcherProvider: DispatcherProvider,
    getAllProgramsUseCase: GetAllProgramsUseCase,
    getSelectedProgramUseCase: GetSelectedProgramUseCase,
    selectProgramUseCase: SelectProgramUseCase,
    private val onOutput: (ProgramsComponent.Output) -> Unit
) : ProgramsComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore {
        ProgramsStoreFactory(
            storeFactory = storeFactory,
            dispatcherProvider = dispatcherProvider,
            getAllProgramsUseCase = getAllProgramsUseCase,
            getSelectedProgramUseCase = getSelectedProgramUseCase,
            selectProgramUseCase = selectProgramUseCase
        ).create()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val state: StateFlow<ProgramsStore.State> = store.stateFlow

    override fun onProgramClick(program: TrainingProgram) {
        onOutput(ProgramsComponent.Output.NavigateToViewProgram(program))
    }

    override fun onProgramSelect(programCode: String) {
        store.accept(ProgramsStore.Intent.SelectProgram(programCode))
    }

    override fun onCreateProgramClick() {
        onOutput(ProgramsComponent.Output.NavigateToCreateProgram)
    }

    override fun onBackClick() {
        onOutput(ProgramsComponent.Output.NavigateBack)
    }
}
