package com.fungorn.trainingcapacity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.fungorn.trainingcapacity.core.common.DispatcherProvider
import com.fungorn.trainingcapacity.core.domain.usecase.AddEntryUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.CreateProgramUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.ExportMesocycleStatisticsUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetAllProgramsUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetEntryByIdUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetMesocycleStatisticsUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetProgramByCodeUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetSelectedProgramUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.SelectProgramUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.StartNewMesocycleUseCase
import com.fungorn.trainingcapacity.feature.dashboard.domain.usecase.GetDashboardDataUseCase
import com.fungorn.trainingcapacity.feature.form.domain.usecase.GetCurrentTrainingContextUseCase
import com.fungorn.trainingcapacity.feature.mesocycles.domain.usecase.GetMesocyclesDataUseCase
import com.fungorn.trainingcapacity.root.DefaultRootComponent
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    
    private val storeFactory: StoreFactory by inject()
    private val dispatcherProvider: DispatcherProvider by inject()
    private val getDashboardDataUseCase: GetDashboardDataUseCase by inject()
    private val addEntryUseCase: AddEntryUseCase by inject()
    private val getEntryByIdUseCase: GetEntryByIdUseCase by inject()
    private val getCurrentTrainingContextUseCase: GetCurrentTrainingContextUseCase by inject()
    private val getMesocyclesDataUseCase: GetMesocyclesDataUseCase by inject()
    private val getMesocycleStatisticsUseCase: GetMesocycleStatisticsUseCase by inject()
    private val exportMesocycleStatisticsUseCase: ExportMesocycleStatisticsUseCase by inject()
    private val startNewMesocycleUseCase: StartNewMesocycleUseCase by inject()
    private val getAllProgramsUseCase: GetAllProgramsUseCase by inject()
    private val getSelectedProgramUseCase: GetSelectedProgramUseCase by inject()
    private val selectProgramUseCase: SelectProgramUseCase by inject()
    private val getProgramByCodeUseCase: GetProgramByCodeUseCase by inject()
    private val createProgramUseCase: CreateProgramUseCase by inject()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        val rootComponent = DefaultRootComponent(
            componentContext = defaultComponentContext(),
            storeFactory = storeFactory,
            dispatcherProvider = dispatcherProvider,
            getDashboardDataUseCase = getDashboardDataUseCase,
            addEntryUseCase = addEntryUseCase,
            getEntryByIdUseCase = getEntryByIdUseCase,
            getCurrentTrainingContextUseCase = getCurrentTrainingContextUseCase,
            getMesocyclesDataUseCase = getMesocyclesDataUseCase,
            getMesocycleStatisticsUseCase = getMesocycleStatisticsUseCase,
            exportMesocycleStatisticsUseCase = exportMesocycleStatisticsUseCase,
            startNewMesocycleUseCase = startNewMesocycleUseCase,
            getAllProgramsUseCase = getAllProgramsUseCase,
            getSelectedProgramUseCase = getSelectedProgramUseCase,
            selectProgramUseCase = selectProgramUseCase,
            getProgramByCodeUseCase = getProgramByCodeUseCase,
            createProgramUseCase = createProgramUseCase
        )

        setContent {
            App(rootComponent)
        }
    }
}