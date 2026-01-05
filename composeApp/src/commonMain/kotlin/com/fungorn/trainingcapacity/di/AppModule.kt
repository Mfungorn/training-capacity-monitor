package com.fungorn.trainingcapacity.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.fungorn.trainingcapacity.core.common.DefaultDispatcherProvider
import com.fungorn.trainingcapacity.core.common.DispatcherProvider
import com.fungorn.trainingcapacity.core.data.di.dataModule
import com.fungorn.trainingcapacity.core.domain.di.domainModule
import com.fungorn.trainingcapacity.feature.dashboard.di.dashboardModule
import com.fungorn.trainingcapacity.feature.form.di.formModule
import com.fungorn.trainingcapacity.feature.mesocycles.di.mesocyclesModule
import com.fungorn.trainingcapacity.feature.programs.di.programsModule
import org.koin.dsl.module

val appModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
    single<StoreFactory> { DefaultStoreFactory() }
}

val allModules = listOf(
    appModule,
    dataModule,
    domainModule,
    dashboardModule,
    formModule,
    mesocyclesModule,
    programsModule
)
