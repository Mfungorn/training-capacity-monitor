package com.fungorn.trainingcapacity.feature.mesocycles.di

import com.fungorn.trainingcapacity.feature.mesocycles.domain.usecase.GetMesocyclesDataUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val mesocyclesModule = module {
    factoryOf(::GetMesocyclesDataUseCase)
}
