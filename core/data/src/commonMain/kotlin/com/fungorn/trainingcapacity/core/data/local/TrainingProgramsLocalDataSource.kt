package com.fungorn.trainingcapacity.core.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.fungorn.trainingcapacity.core.data.local.dto.TrainingDayDto
import com.fungorn.trainingcapacity.core.data.local.dto.TrainingProgramDto
import com.fungorn.trainingcapacity.core.data.local.dto.TrainingProgramsDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class TrainingProgramsLocalDataSource(
    private val dataStore: DataStore<Preferences>
) {
    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private val PROGRAMS_KEY = stringPreferencesKey("training_programs")
        private val SELECTED_PROGRAM_KEY = stringPreferencesKey("selected_program_code")
        private const val DEFAULT_PROGRAM_CODE = "BR0"
    }

    private val builtInPrograms = listOf(
        TrainingProgramDto(
            code = "UL1",
            name = "Upper-Lower",
            trainingDays = listOf(
                TrainingDayDto(
                    id = "UL1_D1",
                    name = "Day #1",
                    muscleGroups = listOf(
                        "CHEST",
                        "BACK",
                        "FRONT_DELTS",
                        "SIDE_DELTS",
                        "REAR_DELTS",
                        "BICEPS",
                        "TRICEPS"
                    )
                ),
                TrainingDayDto(
                    id = "UL1_D2",
                    name = "Day #2",
                    muscleGroups = listOf(
                        "QUADS",
                        "HAMSTRINGS",
                        "GLUTES",
                        "CALVES",
                        "SIDE_DELTS",
                        "BICEPS",
                        "TRICEPS"
                    )
                ),
                TrainingDayDto(
                    id = "UL1_D3",
                    name = "Day #3",
                    muscleGroups = listOf(
                        "CHEST",
                        "BACK",
                        "FRONT_DELTS",
                        "SIDE_DELTS",
                        "REAR_DELTS",
                        "BICEPS",
                        "TRICEPS"
                    )
                ),
                TrainingDayDto(
                    id = "UL1_D4",
                    name = "Day #4",
                    muscleGroups = listOf(
                        "QUADS",
                        "HAMSTRINGS",
                        "GLUTES",
                        "CALVES",
                        "SIDE_DELTS",
                        "BICEPS",
                        "TRICEPS"
                    )
                )
            ),
            isBuiltIn = true
        ),
        TrainingProgramDto(
            code = "BR0",
            name = "Bro-split",
            trainingDays = listOf(
                TrainingDayDto(
                    id = "BR0_D1",
                    name = "Chest + Biceps",
                    muscleGroups = listOf("CHEST", "BICEPS")
                ),
                TrainingDayDto(
                    id = "BR0_D2",
                    name = "Back + Triceps",
                    muscleGroups = listOf("BACK", "TRICEPS")
                ),
                TrainingDayDto(
                    id = "BR0_D3",
                    name = "Legs + Delts",
                    muscleGroups = listOf(
                        "QUADS", "HAMSTRINGS", "GLUTES", "CALVES",
                        "FRONT_DELTS", "SIDE_DELTS", "REAR_DELTS"
                    )
                )
            ),
            isBuiltIn = true
        )
    )

    fun getAllPrograms(): Flow<List<TrainingProgramDto>> = dataStore.data.map { preferences ->
        val customPrograms = preferences[PROGRAMS_KEY]?.let {
            try {
                json.decodeFromString<TrainingProgramsDto>(it).programs
            } catch (e: Exception) {
                emptyList()
            }
        } ?: emptyList()
        builtInPrograms + customPrograms
    }

    fun getProgramByCode(code: String): Flow<TrainingProgramDto?> =
        getAllPrograms().map { programs ->
            programs.find { it.code == code }
        }

    suspend fun createProgram(program: TrainingProgramDto) {
        dataStore.edit { preferences ->
            val currentPrograms = preferences[PROGRAMS_KEY]?.let {
                try {
                    json.decodeFromString<TrainingProgramsDto>(it).programs
                } catch (e: Exception) {
                    emptyList()
                }
            } ?: emptyList()

            val updatedPrograms = currentPrograms.filter { it.code != program.code } + program
            preferences[PROGRAMS_KEY] = json.encodeToString(TrainingProgramsDto(updatedPrograms))
        }
    }

    suspend fun deleteProgram(code: String) {
        dataStore.edit { preferences ->
            val currentPrograms = preferences[PROGRAMS_KEY]?.let {
                try {
                    json.decodeFromString<TrainingProgramsDto>(it).programs
                } catch (e: Exception) {
                    emptyList()
                }
            } ?: emptyList()

            val updatedPrograms = currentPrograms.filter { it.code != code }
            preferences[PROGRAMS_KEY] = json.encodeToString(TrainingProgramsDto(updatedPrograms))
        }
    }

    fun getSelectedProgram(): Flow<TrainingProgramDto?> = dataStore.data.map { preferences ->
        val selectedCode = preferences[SELECTED_PROGRAM_KEY] ?: DEFAULT_PROGRAM_CODE
        val allPrograms = getAllProgramsSync(preferences)
        allPrograms.find { it.code == selectedCode } ?: allPrograms.firstOrNull()
    }

    suspend fun selectProgram(code: String) {
        dataStore.edit { preferences ->
            preferences[SELECTED_PROGRAM_KEY] = code
        }
    }

    private fun getAllProgramsSync(preferences: Preferences): List<TrainingProgramDto> {
        val customPrograms = preferences[PROGRAMS_KEY]?.let {
            try {
                json.decodeFromString<TrainingProgramsDto>(it).programs
            } catch (e: Exception) {
                emptyList()
            }
        } ?: emptyList()
        return builtInPrograms + customPrograms
    }
}
