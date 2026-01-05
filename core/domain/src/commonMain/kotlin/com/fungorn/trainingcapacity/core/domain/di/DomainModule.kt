package com.fungorn.trainingcapacity.core.domain.di

import com.fungorn.trainingcapacity.core.domain.usecase.AddEntryUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetActiveMesocycleUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetAllEntriesUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetAllMesocyclesUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetEntryByIdUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.SelectMesocycleUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.StartNewMesocycleUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::GetAllMesocyclesUseCase)
    factoryOf(::StartNewMesocycleUseCase)
    factoryOf(::SelectMesocycleUseCase)
    factoryOf(::GetActiveMesocycleUseCase)
    factoryOf(::GetAllEntriesUseCase)
    factoryOf(::GetEntryByIdUseCase)
    factoryOf(::AddEntryUseCase)
}
