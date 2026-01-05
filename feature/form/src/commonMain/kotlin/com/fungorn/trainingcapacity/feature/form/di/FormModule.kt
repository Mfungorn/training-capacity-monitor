package com.fungorn.trainingcapacity.feature.form.di

import com.fungorn.trainingcapacity.feature.form.domain.usecase.GetCurrentTrainingContextUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val formModule = module {
    factoryOf(::GetCurrentTrainingContextUseCase)
}
