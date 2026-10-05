package com.fungorn.trainingcapacity

import android.app.Application
import com.fungorn.trainingcapacity.core.domain.export.FileExporter
import com.fungorn.trainingcapacity.di.allModules
import com.fungorn.trainingcapacity.di.createDataStore
import com.fungorn.trainingcapacity.export.AndroidFileExporter
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class TrainingCapacityApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger()
            androidContext(this@TrainingCapacityApplication)
            modules(
                module {
                    single { createDataStore(this@TrainingCapacityApplication) }
                    single<FileExporter> { AndroidFileExporter(androidContext()) }
                }
            )
            modules(allModules)
        }
    }
}
