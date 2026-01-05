package com.fungorn.trainingcapacity.feature.form.presentation.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.fungorn.trainingcapacity.core.domain.usecase.AddEntryUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetEntryByIdUseCase
import com.fungorn.trainingcapacity.feature.form.presentation.store.FormStore
import com.fungorn.trainingcapacity.feature.form.presentation.store.FormStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DefaultFormComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    addEntryUseCase: AddEntryUseCase,
    getEntryByIdUseCase: GetEntryByIdUseCase,
    private val entryId: String?,
    private val onOutput: (FormComponent.Output) -> Unit
) : FormComponent, ComponentContext by componentContext {
    
    private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
    
    private val store = instanceKeeper.getStore {
        FormStoreFactory(
            storeFactory = storeFactory,
            addEntryUseCase = addEntryUseCase,
            getEntryByIdUseCase = getEntryByIdUseCase
        ).create(entryId)
    }
    
    init {
        lifecycle.doOnDestroy { scope.cancel() }
        
        entryId?.let {
            store.accept(FormStore.Intent.LoadEntry(it))
        }
        
        store.labels
            .onEach { label ->
                when (label) {
                    is FormStore.Label.EntrySaved -> onOutput(FormComponent.Output.NavigateBack)
                    is FormStore.Label.ShowError -> { /* Handle error */ }
                }
            }
            .launchIn(scope)
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    override val state: StateFlow<FormStore.State> = store.stateFlow
    
    override fun onSaveClick() {
        store.accept(FormStore.Intent.SaveEntry)
    }
    
    override fun onBackClick() {
        onOutput(FormComponent.Output.NavigateBack)
    }
}
