package com.fungorn.trainingcapacity.feature.dashboard.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.fungorn.trainingcapacity.core.domain.usecase.GetActiveMesocycleUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetAllEntriesUseCase
import com.fungorn.trainingcapacity.feature.dashboard.store.DashboardStore
import com.fungorn.trainingcapacity.feature.dashboard.store.DashboardStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class DefaultDashboardComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    getActiveMesocycleUseCase: GetActiveMesocycleUseCase,
    getAllEntriesUseCase: GetAllEntriesUseCase,
    private val onOutput: (DashboardComponent.Output) -> Unit
) : DashboardComponent, ComponentContext by componentContext {
    
    private val store = instanceKeeper.getStore {
        DashboardStoreFactory(
            storeFactory = storeFactory,
            getActiveMesocycleUseCase = getActiveMesocycleUseCase,
            getAllEntriesUseCase = getAllEntriesUseCase,
        ).create()
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    override val state: StateFlow<DashboardStore.State> = store.stateFlow

    override fun onMesocycleClick() {
        onOutput(DashboardComponent.Output.NavigateToMesocycles)
    }

    override fun onProgramClick() {
        onOutput(DashboardComponent.Output.NavigateToPrograms)
    }

    override fun onAddClick() {
        onOutput(DashboardComponent.Output.NavigateToForm)
    }
}
