package com.fungorn.trainingcapacity.feature.mesocycles.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fungorn.trainingcapacity.core.common.formatMillisToDate
import com.fungorn.trainingcapacity.core.domain.model.TrainingMesocycle
import com.fungorn.trainingcapacity.core.ui.components.LoadingIndicator
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.component.MesocycleDetailComponent
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.model.RirRange
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.model.WeekState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MesocycleDetailContent(component: MesocycleDetailComponent) {
    val state by component.state.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (state.isCreateMode) "New Mesocycle" else "View Mesocycle")
                },
                navigationIcon = {
                    IconButton(onClick = { component.onCancelClick() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            ) {
                // Start Date section (fixed at top)
                Text(
                    text = "Start Date",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = formatMillisToDate(state.startDateMillis),
                    onValueChange = {},
                    readOnly = true,
                    enabled = state.isCreateMode,
                    trailingIcon = {
                        if (state.isCreateMode) {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = "Select date"
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Weeks header with Add button (fixed)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Weeks",
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (state.isCreateMode) {
                        TextButton(onClick = { component.onAddWeek() }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Text("Add Week")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable weeks list
                val listState = rememberLazyListState()
                val weeksCount = state.weeks.size

                LaunchedEffect(weeksCount) {
                    if (weeksCount > 0) {
                        listState.animateScrollToItem(weeksCount - 1)
                    }
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(state.weeks) { index, week ->
                        WeekCard(
                            index = index,
                            week = week,
                            isEditable = state.isCreateMode,
                            canRemove = state.weeks.size > 1,
                            onUpdateWeek = { type, rirRange ->
                                component.onUpdateWeek(index, type, rirRange)
                            },
                            onRemove = { component.onRemoveWeek(index) }
                        )
                    }
                }

                // Action buttons (fixed at bottom)
                if (state.isCreateMode) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { component.onSaveClick() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading
                    ) {
                        Text("Start")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { component.onCancelClick() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.startDateMillis
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            component.onStartDateChange(it)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeekCard(
    index: Int,
    week: WeekState,
    isEditable: Boolean,
    canRemove: Boolean,
    onUpdateWeek: (TrainingMesocycle.Week.Type, RirRange) -> Unit,
    onRemove: () -> Unit
) {
    var typeExpanded by remember { mutableStateOf(false) }
    var rirExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Week ${index + 1}",
                    style = MaterialTheme.typography.titleSmall
                )
                if (isEditable && canRemove) {
                    IconButton(onClick = onRemove) {
                        Icon(Icons.Default.Close, contentDescription = "Remove week")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = typeExpanded && isEditable,
                    onExpandedChange = { if (isEditable) typeExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = week.type.name.lowercase().replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        readOnly = true,
                        enabled = isEditable,
                        label = { Text("Week type") },
                        trailingIcon = {
                            if (isEditable) ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded)
                        },
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    )

                    ExposedDropdownMenu(
                        expanded = typeExpanded && isEditable,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        TrainingMesocycle.Week.Type.entries.forEach { type ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        type.name.lowercase().replaceFirstChar { it.uppercase() })
                                },
                                onClick = {
                                    onUpdateWeek(type, week.rirRange)
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = rirExpanded && isEditable,
                    onExpandedChange = { if (isEditable) rirExpanded = it },
                    modifier = Modifier.width(120.dp)
                ) {
                    OutlinedTextField(
                        value = week.rirRange.label,
                        onValueChange = {},
                        readOnly = true,
                        enabled = isEditable,
                        label = { Text("RIR") },
                        trailingIcon = {
                            if (isEditable) ExposedDropdownMenuDefaults.TrailingIcon(expanded = rirExpanded)
                        },
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    )

                    ExposedDropdownMenu(
                        expanded = rirExpanded && isEditable,
                        onDismissRequest = { rirExpanded = false }
                    ) {
                        RirRange.entries.forEach { range ->
                            DropdownMenuItem(
                                text = { Text(range.label) },
                                onClick = {
                                    onUpdateWeek(week.type, range)
                                    rirExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
