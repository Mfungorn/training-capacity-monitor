package com.fungorn.trainingcapacity.feature.dashboard.di

import com.fungorn.trainingcapacity.feature.dashboard.domain.usecase.GetDashboardDataUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val dashboardModule = module {
    factoryOf(::GetDashboardDataUseCase)
}
