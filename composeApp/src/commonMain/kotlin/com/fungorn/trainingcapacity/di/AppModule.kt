package com.fungorn.trainingcapacity.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.fungorn.trainingcapacity.core.common.DefaultDispatcherProvider
import com.fungorn.trainingcapacity.core.common.DispatcherProvider
import com.fungorn.trainingcapacity.core.data.local.TrainingEntriesLocalDataSource
import com.fungorn.trainingcapacity.core.data.local.TrainingMesocyclesLocalDataSource
import com.fungorn.trainingcapacity.core.data.repository.TrainingRepositoryImpl
import com.fungorn.trainingcapacity.core.domain.repository.TrainingRepository
import com.fungorn.trainingcapacity.core.domain.usecase.AddEntryUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetActiveMesocycleUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetAllEntriesUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetAllMesocyclesUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.SelectMesocycleUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.StartNewMesocycleUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
    single<StoreFactory> { DefaultStoreFactory() }
    
    singleOf(::TrainingMesocyclesLocalDataSource)
    singleOf(::TrainingEntriesLocalDataSource)
    singleOf(::TrainingRepositoryImpl) bind TrainingRepository::class

    factoryOf(::GetAllMesocyclesUseCase)
    factoryOf(::StartNewMesocycleUseCase)
    factoryOf(::SelectMesocycleUseCase)
    factoryOf(::GetActiveMesocycleUseCase)

    factoryOf(::GetAllEntriesUseCase)
    factoryOf(::AddEntryUseCase)
}
