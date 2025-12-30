package com.fungorn.trainingcapacity.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.fungorn.trainingcapacity.core.common.DefaultDispatcherProvider
import com.fungorn.trainingcapacity.core.common.DispatcherProvider
import com.fungorn.trainingcapacity.core.data.local.LocalDataSource
import com.fungorn.trainingcapacity.core.data.repository.TrainingRepositoryImpl
import com.fungorn.trainingcapacity.core.domain.repository.TrainingRepository
import com.fungorn.trainingcapacity.core.domain.usecase.AddEntryUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.DeleteEntryUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetAllEntriesUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
    single<StoreFactory> { DefaultStoreFactory() }
    
    singleOf(::LocalDataSource)
    singleOf(::TrainingRepositoryImpl) bind TrainingRepository::class
    
    factoryOf(::GetAllEntriesUseCase)
    factoryOf(::AddEntryUseCase)
    factoryOf(::DeleteEntryUseCase)
}
