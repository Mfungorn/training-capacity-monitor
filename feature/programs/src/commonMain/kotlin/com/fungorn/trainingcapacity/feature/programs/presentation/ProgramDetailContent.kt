package com.fungorn.trainingcapacity.feature.programs.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fungorn.trainingcapacity.core.domain.model.TrainingGroup
import com.fungorn.trainingcapacity.feature.programs.presentation.component.ProgramDetailComponent
import com.fungorn.trainingcapacity.feature.programs.presentation.store.ProgramDetailStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramDetailContent(component: ProgramDetailComponent) {
    val state by component.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Program") },
                navigationIcon = {
                    IconButton(onClick = component::onCancelClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (state.isCreateMode) {
                OutlinedTextField(
                    value = state.programName,
                    onValueChange = component::onProgramNameChange,
                    label = { Text("Program name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            } else {
                Text(
                    text = state.programName,
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Training days",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(state.trainingDays) { index, day ->
                    TrainingDayCard(
                        day = day,
                        isEditable = state.isCreateMode,
                        canRemove = state.trainingDays.size > 1,
                        onToggleExpanded = { component.onToggleDayExpanded(index) },
                        onToggleMuscleGroup = { group ->
                            component.onToggleMuscleGroup(index, group)
                        },
                        onRemove = { component.onRemoveTrainingDay(index) }
                    )
                }

                if (state.isCreateMode) {
                    item {
                        OutlinedButton(
                            onClick = component::onAddTrainingDay,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("Add training day")
                        }
                    }
                }
            }

            if (state.isCreateMode) {
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = component::onSaveClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.programName.isNotBlank() &&
                            state.trainingDays.all { it.muscleGroups.isNotEmpty() }
                ) {
                    Text("Create")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = component::onCancelClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
private fun TrainingDayCard(
    day: ProgramDetailStore.TrainingDayState,
    isEditable: Boolean,
    canRemove: Boolean,
    onToggleExpanded: () -> Unit,
    onToggleMuscleGroup: (TrainingGroup) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = day.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                if (isEditable && canRemove) {
                    IconButton(onClick = onRemove) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Remove day"
                        )
                    }
                }

                IconButton(onClick = onToggleExpanded) {
                    Icon(
                        if (day.isExpanded)
                            Icons.Default.KeyboardArrowUp
                        else
                            Icons.Default.KeyboardArrowDown,
                        contentDescription = if (day.isExpanded) "Collapse" else "Expand"
                    )
                }
            }

            AnimatedVisibility(visible = day.isExpanded) {
                Column(
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    TrainingGroup.entries.forEach { group ->
                        val isSelected = group in day.muscleGroups

                        when {
                            isEditable -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = { onToggleMuscleGroup(group) }
                                    )
                                    Text(
                                        text = group.displayName,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            isSelected -> {
                                Text(
                                    text = group.displayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (!day.isExpanded && day.muscleGroups.isNotEmpty()) {
                Text(
                    text = day.muscleGroups.joinToString(", ") { it.displayName },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

private val TrainingGroup.displayName: String
    get() = when (this) {
        TrainingGroup.CHEST -> "Chest"
        TrainingGroup.BACK -> "Back"
        TrainingGroup.DELTS -> "Delts"
        TrainingGroup.BICEPS -> "Biceps"
        TrainingGroup.TRICEPS -> "Triceps"
        TrainingGroup.LEGS -> "Legs"
    }
