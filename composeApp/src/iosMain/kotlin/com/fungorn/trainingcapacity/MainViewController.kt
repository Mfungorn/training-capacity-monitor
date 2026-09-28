package com.fungorn.trainingcapacity

import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.fungorn.trainingcapacity.core.common.DispatcherProvider
import com.fungorn.trainingcapacity.core.domain.usecase.AddEntryUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.CreateProgramUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetAllProgramsUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetEntryByIdUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetMesocycleByIdUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetProgramByCodeUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetSelectedProgramUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.SelectProgramUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.StartNewMesocycleUseCase
import com.fungorn.trainingcapacity.di.allModules
import com.fungorn.trainingcapacity.di.createDataStore
import com.fungorn.trainingcapacity.feature.dashboard.domain.usecase.GetDashboardDataUseCase
import com.fungorn.trainingcapacity.feature.form.domain.usecase.GetCurrentTrainingContextUseCase
import com.fungorn.trainingcapacity.feature.mesocycles.domain.usecase.GetMesocyclesDataUseCase
import com.fungorn.trainingcapacity.root.DefaultRootComponent
import org.koin.core.context.startKoin
import org.koin.dsl.module

private val koin = startKoin {
    modules(
        module { single { createDataStore() } }
    )
    modules(allModules)
}.koin

private val lifecycle = LifecycleRegistry()

private val rootComponent = DefaultRootComponent(
    componentContext = DefaultComponentContext(lifecycle = lifecycle),
    storeFactory = koin.get<StoreFactory>(),
    dispatcherProvider = koin.get<DispatcherProvider>(),
    getDashboardDataUseCase = koin.get<GetDashboardDataUseCase>(),
    addEntryUseCase = koin.get<AddEntryUseCase>(),
    getEntryByIdUseCase = koin.get<GetEntryByIdUseCase>(),
    getCurrentTrainingContextUseCase = koin.get<GetCurrentTrainingContextUseCase>(),
    getMesocyclesDataUseCase = koin.get<GetMesocyclesDataUseCase>(),
    getMesocycleByIdUseCase = koin.get<GetMesocycleByIdUseCase>(),
    startNewMesocycleUseCase = koin.get<StartNewMesocycleUseCase>(),
    getAllProgramsUseCase = koin.get<GetAllProgramsUseCase>(),
    getSelectedProgramUseCase = koin.get<GetSelectedProgramUseCase>(),
    selectProgramUseCase = koin.get<SelectProgramUseCase>(),
    getProgramByCodeUseCase = koin.get<GetProgramByCodeUseCase>(),
    createProgramUseCase = koin.get<CreateProgramUseCase>()
)

fun MainViewController() = ComposeUIViewController {
    App(rootComponent)
}