package com.fungorn.trainingcapacity.feature.programs.presentation.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.fungorn.trainingcapacity.core.domain.model.TrainingProgram
import com.fungorn.trainingcapacity.feature.programs.presentation.store.ProgramsStore
import com.fungorn.trainingcapacity.feature.programs.presentation.store.ProgramsStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class DefaultProgramsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val onOutput: (ProgramsComponent.Output) -> Unit
) : ProgramsComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore {
        ProgramsStoreFactory(storeFactory = storeFactory).create()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val state: StateFlow<ProgramsStore.State> = store.stateFlow

    override fun onProgramClick(program: TrainingProgram) {
        store.accept(ProgramsStore.Intent.SelectProgram(program))
    }

    override fun onBackClick() {
        onOutput(ProgramsComponent.Output.NavigateBack)
    }
}
