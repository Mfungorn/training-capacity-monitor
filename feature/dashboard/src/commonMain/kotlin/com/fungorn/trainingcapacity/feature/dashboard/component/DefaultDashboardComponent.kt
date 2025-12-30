package com.fungorn.trainingcapacity.feature.dashboard.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.fungorn.trainingcapacity.core.domain.usecase.DeleteEntryUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetAllEntriesUseCase
import com.fungorn.trainingcapacity.feature.dashboard.store.DashboardStore
import com.fungorn.trainingcapacity.feature.dashboard.store.DashboardStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class DefaultDashboardComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    getAllEntriesUseCase: GetAllEntriesUseCase,
    deleteEntryUseCase: DeleteEntryUseCase,
    private val onOutput: (DashboardComponent.Output) -> Unit
) : DashboardComponent, ComponentContext by componentContext {
    
    private val store = instanceKeeper.getStore {
        DashboardStoreFactory(
            storeFactory = storeFactory,
            getAllEntriesUseCase = getAllEntriesUseCase,
            deleteEntryUseCase = deleteEntryUseCase
        ).create()
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    override val state: StateFlow<DashboardStore.State> = store.stateFlow
    
    override fun onAddClick() {
        onOutput(DashboardComponent.Output.NavigateToForm)
    }
    
    override fun onEntryClick(id: String) {
        onOutput(DashboardComponent.Output.NavigateToEdit(id))
    }
    
    override fun onDeleteClick(id: String) {
        store.accept(DashboardStore.Intent.DeleteEntry(id))
    }
}
