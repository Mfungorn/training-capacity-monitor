package com.fungorn.trainingcapacity.feature.mesocycles.presentation.component

import com.fungorn.trainingcapacity.core.domain.model.TrainingMesocycle
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.model.RirRange
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.store.MesocycleDetailStore
import kotlinx.coroutines.flow.StateFlow

interface MesocycleDetailComponent {

    val state: StateFlow<MesocycleDetailStore.State>

    fun onStartDateChange(millis: Long)
    fun onAddWeek()
    fun onUpdateWeek(index: Int, type: TrainingMesocycle.Week.Type, rirRange: RirRange)
    fun onRemoveWeek(index: Int)
    fun onSaveClick()
    fun onCancelClick()

    sealed interface Output {
        data object NavigateBack : Output
    }
}
