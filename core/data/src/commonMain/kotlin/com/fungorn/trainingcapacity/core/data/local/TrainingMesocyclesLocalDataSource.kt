package com.fungorn.trainingcapacity.core.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.fungorn.trainingcapacity.core.data.local.dto.TrainingMesocycleDto
import com.fungorn.trainingcapacity.core.data.local.dto.TrainingMesocyclesDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlin.time.Clock

class TrainingMesocyclesLocalDataSource(
    private val dataStore: DataStore<Preferences>
) {
    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private val MESOCYCLES_KEY = stringPreferencesKey("training_mesocycles")
    }

    fun getAllCycles(): Flow<List<TrainingMesocycleDto>> = dataStore.data.map { preferences ->
        val cyclesJson = preferences[MESOCYCLES_KEY] ?: return@map emptyList()
        try {
            json.decodeFromString<TrainingMesocyclesDto>(cyclesJson).cycles
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getCycleById(id: String): Flow<TrainingMesocycleDto?> = getAllCycles().map { cycles ->
        cycles.find { it.id == id }
    }

    suspend fun createCycle(cycle: TrainingMesocycleDto) {
        dataStore.edit { preferences ->
            val currentCycles = preferences[MESOCYCLES_KEY]?.let {
                try {
                    json.decodeFromString<TrainingMesocyclesDto>(it).cycles
                } catch (e: Exception) {
                    listOf()
                }
            } ?: listOf()

            var didChange = false
            var updatedCycles = currentCycles.map { value ->
                if (value.id == cycle.id) {
                    didChange = true
                    cycle.copy(isSelected = true)
                } else {
                    value.copy(isSelected = false)
                }
            }
            if (!didChange) {
                updatedCycles = updatedCycles + cycle.copy(isSelected = true)
            }
            preferences[MESOCYCLES_KEY] =
                json.encodeToString(TrainingMesocyclesDto(updatedCycles))
        }
    }

    suspend fun selectCycle(id: String) {
        dataStore.edit { preferences ->
            val currentCycles = preferences[MESOCYCLES_KEY]?.let {
                try {
                    json.decodeFromString<TrainingMesocyclesDto>(it).cycles
                } catch (e: Exception) {
                    listOf()
                }
            } ?: listOf()

            val updatedCycles = currentCycles.map { value ->
                value.copy(isSelected = value.id == id)
            }
            preferences[MESOCYCLES_KEY] =
                json.encodeToString(TrainingMesocyclesDto(updatedCycles))
        }
    }

    fun getSelectedCycle() = getAllCycles().map { cycles ->
        cycles.find(TrainingMesocycleDto::isSelected)
    }

    fun isProgramUsedByAnyCycle(programCode: String): Flow<Boolean> = getAllCycles().map { cycles ->
        cycles.any { it.programCode == programCode }
    }

    // TODO : will be changed later
    private val defaultSelectedMesocycle
        get() = TrainingMesocycleDto(
            id = "0",
            programCode = "UL",
            isSelected = true,
            weeks = listOf(
                TrainingMesocycleDto.WeekDto(
                    name = "ADAPT",
                    maximumCapacityLowerEnd = 0,
                    maximumCapacityUpperEnd = 2,
                ),
                TrainingMesocycleDto.WeekDto(
                    name = "WORKING",
                    maximumCapacityLowerEnd = 4,
                    maximumCapacityUpperEnd = 6,
                ),
                TrainingMesocycleDto.WeekDto(
                    name = "WORKING",
                    maximumCapacityLowerEnd = 6,
                    maximumCapacityUpperEnd = 8,
                ),
                TrainingMesocycleDto.WeekDto(
                    name = "WORKING",
                    maximumCapacityLowerEnd = 8,
                    maximumCapacityUpperEnd = 10,
                ),
                TrainingMesocycleDto.WeekDto(
                    name = "WORKING",
                    maximumCapacityLowerEnd = 8,
                    maximumCapacityUpperEnd = 10,
                ),
                TrainingMesocycleDto.WeekDto(
                    name = "WORKING",
                    maximumCapacityLowerEnd = 6,
                    maximumCapacityUpperEnd = 8,
                ),
                TrainingMesocycleDto.WeekDto(
                    name = "DELOAD",
                    maximumCapacityLowerEnd = 0,
                    maximumCapacityUpperEnd = 0,
                ),
            ),
            startedAt = Clock.System.now().toEpochMilliseconds()
        )
}
