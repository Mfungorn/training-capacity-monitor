package com.fungorn.trainingcapacity

import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.fungorn.trainingcapacity.core.domain.repository.TrainingRepository
import com.fungorn.trainingcapacity.core.domain.usecase.AddEntryUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetActiveMesocycleUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetAllEntriesUseCase
import com.fungorn.trainingcapacity.di.appModule
import com.fungorn.trainingcapacity.di.createDataStore
import com.fungorn.trainingcapacity.root.DefaultRootComponent
import org.koin.core.context.startKoin
import org.koin.dsl.module

private val koin = startKoin {
    modules(
        module { single { createDataStore() } },
        appModule
    )
}.koin

private val lifecycle = LifecycleRegistry()

private val rootComponent = DefaultRootComponent(
    componentContext = DefaultComponentContext(lifecycle = lifecycle),
    storeFactory = koin.get<StoreFactory>(),
    getActiveMesocycleUseCase = koin.get<GetActiveMesocycleUseCase>(),
    getAllEntriesUseCase = koin.get<GetAllEntriesUseCase>(),
    addEntryUseCase = koin.get<AddEntryUseCase>(),
    repository = koin.get<TrainingRepository>()
)

fun MainViewController() = ComposeUIViewController {
    App(rootComponent)
}