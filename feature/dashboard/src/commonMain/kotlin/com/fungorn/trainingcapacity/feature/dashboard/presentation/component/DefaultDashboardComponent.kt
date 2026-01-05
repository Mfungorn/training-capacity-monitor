package com.fungorn.trainingcapacity.feature.dashboard.presentation.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.Lifecycle
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

    init {
        lifecycle.subscribe(
            object : Lifecycle.Callbacks {
                override fun onResume() {
                    store.accept(DashboardStore.Intent.Refresh)
                }
            }
        )
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    override val state: StateFlow<DashboardStore.State> = store.stateFlow

    override fun onRefresh() {
        store.accept(DashboardStore.Intent.Refresh)
    }

    override fun onMesocyclesClick() {
        onOutput(DashboardComponent.Output.NavigateToMesocycles)
    }

    override fun onProgramsClick() {
        onOutput(DashboardComponent.Output.NavigateToPrograms)
    }

    override fun onAddClick() {
        onOutput(DashboardComponent.Output.NavigateToForm)
    }
}
