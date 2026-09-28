package com.fungorn.trainingcapacity.core.data.di

import com.fungorn.trainingcapacity.core.data.local.TrainingEntriesLocalDataSource
import com.fungorn.trainingcapacity.core.data.local.TrainingMesocyclesLocalDataSource
import com.fungorn.trainingcapacity.core.data.local.TrainingProgramsLocalDataSource
import com.fungorn.trainingcapacity.core.data.repository.EntryRepositoryImpl
import com.fungorn.trainingcapacity.core.data.repository.MesocycleRepositoryImpl
import com.fungorn.trainingcapacity.core.data.repository.ProgramRepositoryImpl
import com.fungorn.trainingcapacity.core.domain.repository.EntryRepository
import com.fungorn.trainingcapacity.core.domain.repository.MesocycleRepository
import com.fungorn.trainingcapacity.core.domain.repository.ProgramRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    singleOf(::TrainingMesocyclesLocalDataSource)
    singleOf(::TrainingEntriesLocalDataSource)
    singleOf(::TrainingProgramsLocalDataSource)
    singleOf(::MesocycleRepositoryImpl) bind MesocycleRepository::class
    singleOf(::EntryRepositoryImpl) bind EntryRepository::class
    singleOf(::ProgramRepositoryImpl) bind ProgramRepository::class
}
