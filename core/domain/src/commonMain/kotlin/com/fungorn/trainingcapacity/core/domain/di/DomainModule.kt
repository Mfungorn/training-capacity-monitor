package com.fungorn.trainingcapacity.core.domain.di

import com.fungorn.trainingcapacity.core.domain.usecase.AddEntryUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.CreateProgramUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetActiveMesocycleUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetAllEntriesUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetAllMesocyclesUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetAllProgramsUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetEntryByIdUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetMesocycleByIdUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetProgramByCodeUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetSelectedProgramUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.SelectMesocycleUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.SelectProgramUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.StartNewMesocycleUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::GetAllMesocyclesUseCase)
    factoryOf(::GetMesocycleByIdUseCase)
    factoryOf(::StartNewMesocycleUseCase)
    factoryOf(::SelectMesocycleUseCase)
    factoryOf(::GetActiveMesocycleUseCase)
    factoryOf(::GetAllEntriesUseCase)
    factoryOf(::GetEntryByIdUseCase)
    factoryOf(::AddEntryUseCase)
    factoryOf(::GetAllProgramsUseCase)
    factoryOf(::GetSelectedProgramUseCase)
    factoryOf(::SelectProgramUseCase)
    factoryOf(::CreateProgramUseCase)
    factoryOf(::GetProgramByCodeUseCase)
}
