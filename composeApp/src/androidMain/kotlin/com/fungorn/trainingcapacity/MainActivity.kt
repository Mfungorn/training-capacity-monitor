package com.fungorn.trainingcapacity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.fungorn.trainingcapacity.core.domain.repository.TrainingRepository
import com.fungorn.trainingcapacity.core.domain.usecase.AddEntryUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.DeleteEntryUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetAllEntriesUseCase
import com.fungorn.trainingcapacity.root.DefaultRootComponent
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    
    private val storeFactory: StoreFactory by inject()
    private val getAllEntriesUseCase: GetAllEntriesUseCase by inject()
    private val deleteEntryUseCase: DeleteEntryUseCase by inject()
    private val addEntryUseCase: AddEntryUseCase by inject()
    private val repository: TrainingRepository by inject()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        val rootComponent = DefaultRootComponent(
            componentContext = defaultComponentContext(),
            storeFactory = storeFactory,
            getAllEntriesUseCase = getAllEntriesUseCase,
            deleteEntryUseCase = deleteEntryUseCase,
            addEntryUseCase = addEntryUseCase,
            repository = repository
        )

        setContent {
            App(rootComponent)
        }
    }
}