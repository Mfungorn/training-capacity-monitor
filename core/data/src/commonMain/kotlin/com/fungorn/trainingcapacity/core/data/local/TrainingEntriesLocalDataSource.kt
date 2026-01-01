package com.fungorn.trainingcapacity.core.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.fungorn.trainingcapacity.core.data.local.dto.TrainingEntriesDto
import com.fungorn.trainingcapacity.core.data.local.dto.TrainingEntryDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class TrainingEntriesLocalDataSource(
    private val dataStore: DataStore<Preferences>
) {
    private val json = Json { ignoreUnknownKeys = true }
    
    companion object {
        private val MESOCYCLES_KEY = stringPreferencesKey("training_mesocycles")
        private val PROGRAMS_KEY = stringPreferencesKey("training_programs")
        private val ENTRIES_KEY = stringPreferencesKey("training_entries")
    }
    
    fun getAllEntries(): Flow<List<TrainingEntryDto>> = dataStore.data.map { preferences ->
        val entriesJson = preferences[ENTRIES_KEY] ?: return@map emptyList()
        try {
            json.decodeFromString<TrainingEntriesDto>(entriesJson).entries
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun getEntryById(id: String): Flow<TrainingEntryDto?> = getAllEntries().map { entries ->
        entries.find { it.id == id }
    }
    
    suspend fun saveEntry(entry: TrainingEntryDto) {
        dataStore.edit { preferences ->
            val currentEntries = preferences[ENTRIES_KEY]?.let {
                try {
                    json.decodeFromString<TrainingEntriesDto>(it).entries.toMutableList()
                } catch (e: Exception) {
                    mutableListOf()
                }
            } ?: mutableListOf()
            
            val existingIndex = currentEntries.indexOfFirst { it.id == entry.id }
            if (existingIndex >= 0) {
                currentEntries[existingIndex] = entry
            } else {
                currentEntries.add(entry)
            }
            
            preferences[ENTRIES_KEY] = json.encodeToString(TrainingEntriesDto(currentEntries))
        }
    }
    
    suspend fun deleteEntry(id: String) {
        dataStore.edit { preferences ->
            val currentEntries = preferences[ENTRIES_KEY]?.let {
                try {
                    json.decodeFromString<TrainingEntriesDto>(it).entries.toMutableList()
                } catch (e: Exception) {
                    mutableListOf()
                }
            } ?: mutableListOf()
            
            currentEntries.removeAll { it.id == id }
            preferences[ENTRIES_KEY] = json.encodeToString(TrainingEntriesDto(currentEntries))
        }
    }
}
