package com.fungorn.trainingcapacity.feature.dashboard.presentation.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.fungorn.trainingcapacity.feature.dashboard.domain.usecase.GetDashboardDataUseCase
import com.fungorn.trainingcapacity.feature.dashboard.presentation.store.DashboardStore
import com.fungorn.trainingcapacity.feature.dashboard.presentation.store.DashboardStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class DefaultDashboardComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    getDashboardDataUseCase: GetDashboardDataUseCase,
    private val onOutput: (DashboardComponent.Output) -> Unit
) : DashboardComponent, ComponentContext by componentContext {
    
    private val store = instanceKeeper.getStore {
        DashboardStoreFactory(
            storeFactory = storeFactory,
            getDashboardDataUseCase = getDashboardDataUseCase,
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
