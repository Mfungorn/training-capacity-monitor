package com.fungorn.trainingcapacity.feature.mesocycles.presentation.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.fungorn.trainingcapacity.feature.mesocycles.domain.usecase.GetMesocycleListUseCase
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.store.MesocyclesStore
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.store.MesocyclesStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DefaultMesocyclesComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    getMesocycleListUseCase: GetMesocycleListUseCase,
    private val onOutput: (MesocyclesComponent.Output) -> Unit
) : MesocyclesComponent, ComponentContext by componentContext {

    private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    private val store = instanceKeeper.getStore {
        MesocyclesStoreFactory(
            storeFactory = storeFactory,
            getMesocycleListUseCase = getMesocycleListUseCase
        ).create()
    }

    init {
        lifecycle.doOnDestroy { scope.cancel() }

        store.labels
            .onEach { label ->
                when (label) {
                    is MesocyclesStore.Label.NavigateToCreateMesocycle ->
                        onOutput(MesocyclesComponent.Output.NavigateToCreateMesocycle)

                    is MesocyclesStore.Label.NavigateToViewMesocycle ->
                        onOutput(MesocyclesComponent.Output.NavigateToViewMesocycle(label.id))
                }
            }
            .launchIn(scope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val state: StateFlow<MesocyclesStore.State> = store.stateFlow

    override fun onMesocycleClick(id: String) {
        store.accept(MesocyclesStore.Intent.OpenMesocycle(id))
    }

    override fun onStartNewMesocycleClick() {
        store.accept(MesocyclesStore.Intent.StartNewMesocycle)
    }

    override fun onBackClick() {
        onOutput(MesocyclesComponent.Output.NavigateBack)
    }
}
