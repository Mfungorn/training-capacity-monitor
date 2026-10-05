package com.fungorn.trainingcapacity.feature.form.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fungorn.trainingcapacity.core.domain.model.TrainingEntry
import com.fungorn.trainingcapacity.core.domain.model.TrainingProgram
import com.fungorn.trainingcapacity.core.ui.components.LoadingIndicator
import com.fungorn.trainingcapacity.core.ui.components.NumberPicker
import com.fungorn.trainingcapacity.core.ui.components.RatingColorScale
import com.fungorn.trainingcapacity.core.ui.components.RatingSelector
import com.fungorn.trainingcapacity.feature.form.presentation.component.FormComponent

private val feelingRange = TrainingEntry.MIN_FEELING..TrainingEntry.MAX_FEELING

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionSelector(
    programs: List<TrainingProgram>,
    selectedProgram: TrainingProgram?,
    trainingDayNumber: Int,
    onProgramSelect: (String) -> Unit,
    onTrainingDaySelect: (Int) -> Unit
) {
    var programExpanded by remember { mutableStateOf(false) }
    var dayExpanded by remember { mutableStateOf(false) }
    val days = selectedProgram?.trainingDays.orEmpty()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = programExpanded,
            onExpandedChange = { programExpanded = it },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = selectedProgram?.let { "${it.name} (${it.code})" } ?: "-",
                onValueChange = {},
                readOnly = true,
                label = { Text("Program") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = programExpanded) },
                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(
                expanded = programExpanded,
                onDismissRequest = { programExpanded = false }
            ) {
                programs.forEach { program ->
                    DropdownMenuItem(
                        text = { Text("${program.name} (${program.code})") },
                        onClick = {
                            onProgramSelect(program.code)
                            programExpanded = false
                        }
                    )
                }
            }
        }

        ExposedDropdownMenuBox(
            expanded = dayExpanded,
            onExpandedChange = { if (days.isNotEmpty()) dayExpanded = it },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = days.getOrNull(trainingDayNumber - 1)?.name ?: "Day $trainingDayNumber",
                onValueChange = {},
                readOnly = true,
                enabled = days.isNotEmpty(),
                label = { Text("Training day") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayExpanded) },
                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(
                expanded = dayExpanded,
                onDismissRequest = { dayExpanded = false }
            ) {
                days.forEachIndexed { index, day ->
                    DropdownMenuItem(
                        text = { Text("${index + 1}. ${day.name}") },
                        onClick = {
                            onTrainingDaySelect(index + 1)
                            dayExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormContent(component: FormComponent) {
    val state by component.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(if (state.id == null) "New Entry" else "Edit Entry")
                        val subtitle = listOfNotNull(
                            state.selectedProgram?.let { "${it.name} (${it.code})" },
                            state.sessionTitle
                        ).joinToString(" · ")
                        if (subtitle.isNotEmpty()) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = component::onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            LoadingIndicator()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                SessionSelector(
                    programs = state.programs,
                    selectedProgram = state.selectedProgram,
                    trainingDayNumber = state.trainingDayNumber,
                    onProgramSelect = component::onProgramSelect,
                    onTrainingDaySelect = component::onTrainingDaySelect
                )

                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Session skipped", style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = "Counts against adherence, other fields are ignored",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = !state.isCompleted,
                            onCheckedChange = { component.onCompletedChange(!it) }
                        )
                    }
                }

                if (state.isCompleted) {
                    RatingSelector(
                        label = "Readiness before session",
                        value = state.readiness,
                        onValueChange = component::onReadinessChange,
                        range = feelingRange,
                        colorScale = RatingColorScale.HIGH_IS_GOOD,
                        supportingText = "1 = exhausted, 5 = fully recovered"
                    )
                    NumberPicker(
                        label = "Session length (min)",
                        value = state.durationMinutes,
                        onValueChange = component::onDurationChange,
                        minValue = 0,
                        maxValue = 300,
                        step = 5
                    )
                    NumberPicker(
                        label = "Max. Capacity Sets",
                        value = state.maxCapacitySets,
                        onValueChange = component::onMaxCapacitySetsChange,
                        minValue = 0,
                        maxValue = 99
                    )
                    NumberPicker(
                        label = "Overall Difficulty",
                        value = state.overallDifficulty,
                        onValueChange = component::onOverallDifficultyChange,
                        minValue = 0,
                        maxValue = 3
                    )
                    RatingSelector(
                        label = "Fatigue after session",
                        value = state.fatigue,
                        onValueChange = component::onFatigueChange,
                        range = feelingRange,
                        colorScale = RatingColorScale.LOW_IS_GOOD,
                        supportingText = "1 = fresh, 5 = wrecked"
                    )
                    if (state.trainingDayGroups.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Muscle group fatigue",
                                style = MaterialTheme.typography.titleMedium
                            )
                            state.trainingDayGroups.forEach { group ->
                                RatingSelector(
                                    label = group.displayName,
                                    value = state.muscleGroupFatigue[group],
                                    onValueChange = {
                                        component.onMuscleGroupFatigueChange(
                                            group,
                                            it
                                        )
                                    },
                                    range = feelingRange,
                                    colorScale = RatingColorScale.LOW_IS_GOOD
                                )
                            }
                        }
                    }
                }

                Column {
                    Button(
                        onClick = component::onSaveClick,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = state.canSave
                    ) {
                        Text("Done")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = component::onBackClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}
