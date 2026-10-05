package com.fungorn.trainingcapacity.feature.mesocycles.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import com.fungorn.trainingcapacity.core.common.formatDecimal
import com.fungorn.trainingcapacity.core.common.formatDuration
import com.fungorn.trainingcapacity.core.common.formatMillisToDate
import com.fungorn.trainingcapacity.core.common.formatPercent
import com.fungorn.trainingcapacity.core.domain.model.GroupPriority
import com.fungorn.trainingcapacity.core.domain.model.MesocycleStatistics
import com.fungorn.trainingcapacity.core.domain.model.SessionStatistics
import com.fungorn.trainingcapacity.core.domain.model.TrainingGroup
import com.fungorn.trainingcapacity.core.domain.model.TrainingMesocycle
import com.fungorn.trainingcapacity.core.domain.model.TrainingProgram
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
                },
                actions = {
                    if (!state.isCreateMode && state.statistics != null) {
                        IconButton(
                            onClick = { component.onExportClick() },
                            enabled = !state.isExporting
                        ) {
                            if (state.isExporting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.Share, contentDescription = "Export CSV")
                            }
                        }
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
                val listState = rememberLazyListState()
                val weeksCount = state.weeks.size

                LaunchedEffect(weeksCount) {
                    if (state.isCreateMode && weeksCount > 0) {
                        listState.animateScrollToItem(WEEK_ITEMS_OFFSET + weeksCount - 1)
                    }
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item(key = "start_date") {
                        Column {
                            SectionTitle("Start Date")
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
                        }
                    }

                    item(key = "program") {
                        ProgramSelector(
                            programs = state.programs,
                            selected = state.selectedProgram,
                            isEditable = state.isCreateMode,
                            onSelect = component::onProgramSelect
                        )
                    }

                    state.statistics?.let { statistics ->
                        item(key = "summary") { SummaryCard(statistics) }
                    }

                    item(key = "weeks_header") {
                        Text(
                            text = "Weeks",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    if (state.weeks.isEmpty()) {
                        item(key = "weeks_empty") {
                            Text(
                                text = "Add at least one week to start the mesocycle",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    itemsIndexed(state.weeks, key = { index, _ -> "week_$index" }) { index, week ->
                        WeekCard(
                            index = index,
                            week = week,
                            weekSessions = state.statistics?.weeks?.getOrNull(index)?.sessions,
                            isEditable = state.isCreateMode,
                            onUpdateWeek = { type, rirRange ->
                                component.onUpdateWeek(index, type, rirRange)
                            },
                            onRemove = { component.onRemoveWeek(index) }
                        )
                    }

                    if (state.isCreateMode) {
                        item(key = "add_week") {
                            OutlinedButton(
                                onClick = { component.onAddWeek() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add Week")
                            }
                        }
                    }

                    item(key = "priorities_header") {
                        Column {
                            SectionTitle("Muscle group priorities")
                            Text(
                                text = "Focus: up to ${TrainingMesocycle.MAX_FOCUS_GROUPS} groups that get the extra volume. " +
                                        "Growth: progressed slower. Maintenance: kept at minimum volume.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (state.isCreateMode) {
                        items(TrainingGroup.selectable, key = { "priority_${it.name}" }) { group ->
                            val current = state.groupPriorities[group]
                            GroupPriorityRow(
                                group = group,
                                priority = current,
                                isFocusAvailable = state.canAddFocusGroup || current == GroupPriority.FOCUS,
                                onPriorityChange = { component.onGroupPriorityChange(group, it) }
                            )
                        }
                    } else {
                        item(key = "priorities_summary") {
                            GroupPrioritiesSummary(state.groupPriorities)
                        }
                    }

                    state.statistics?.sessions?.muscleGroupFatigue
                        ?.takeIf { it.isNotEmpty() }
                        ?.let { fatigue ->
                            item(key = "group_fatigue") {
                                Column {
                                    SectionTitle("Average muscle group fatigue")
                                    Card(modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            fatigue.entries.sortedBy { it.key.ordinal }
                                                .forEach { (group, value) ->
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text(
                                                            group.displayName,
                                                            style = MaterialTheme.typography.bodyMedium
                                                        )
                                                        Text(
                                                            formatDecimal(value),
                                                            style = MaterialTheme.typography.bodyMedium
                                                        )
                                                    }
                                                }
                                        }
                                    }
                                }
                            }
                        }
                }

                if (!state.isCreateMode && state.statistics?.sessions?.isComplete == true) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { component.onStartNewMesocycleClick() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Start new mesocycle")
                    }
                }

                if (state.isCreateMode) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { component.onSaveClick() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = state.canStart
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

/** Items placed before the week cards in the list: start date, program and the weeks header. */
private const val WEEK_ITEMS_OFFSET = 3

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProgramSelector(
    programs: List<TrainingProgram>,
    selected: TrainingProgram?,
    isEditable: Boolean,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        SectionTitle("Program")
        ExposedDropdownMenuBox(
            expanded = expanded && isEditable,
            onExpandedChange = { if (isEditable) expanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selected?.label ?: "No program selected",
                onValueChange = {},
                readOnly = true,
                enabled = isEditable,
                isError = isEditable && selected == null,
                supportingText = {
                    Text(
                        if (selected == null) "Create a program on the Programs screen first"
                        else "Every entry is logged against this program; sessions advance week by week"
                    )
                },
                trailingIcon = {
                    if (isEditable) ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(
                expanded = expanded && isEditable,
                onDismissRequest = { expanded = false }
            ) {
                programs.forEach { program ->
                    DropdownMenuItem(
                        text = { Text(program.label) },
                        onClick = {
                            onSelect(program.code)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

private val TrainingProgram.label: String
    get() = "$name ($code) · $trainingDaysCount ${if (trainingDaysCount == 1) "day" else "days"}/week"

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun SummaryCard(statistics: MesocycleStatistics) {
    val sessions = statistics.sessions
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "${statistics.mesocycle.program.name} · ${statistics.mesocycle.weeks.size} weeks",
                style = MaterialTheme.typography.titleSmall
            )
            StatRow(
                label = "Adherence",
                value = sessions.adherence?.let(::formatPercent) ?: "-",
                detail = "${sessions.completedSessions} of ${sessions.plannedSessions} sessions" +
                        if (sessions.skippedSessions > 0) ", ${sessions.skippedSessions} skipped" else ""
            )
            StatRow(
                label = "Adherence to date",
                value = sessions.adherenceToDate?.let(::formatPercent) ?: "-",
                detail = "${sessions.loggedSessions} logged"
            )
            StatRow(
                label = "Training time",
                value = formatDuration(sessions.totalDurationMinutes),
                detail = sessions.averageDurationMinutes?.let { "avg ${formatDecimal(it)} min / session" }
            )
            StatRow(
                label = "Readiness / Fatigue",
                value = "${sessions.averageReadiness.orDash()} / ${sessions.averageFatigue.orDash()}",
                detail = "average, 1-5"
            )
            StatRow(
                label = "Max capacity sets",
                value = "${sessions.spentMaximumCapacitySets}",
                detail = sessions.averageDifficulty?.let { "avg difficulty ${formatDecimal(it)}" }
            )
        }
    }
}

@Composable
private fun StatRow(label: String, value: String, detail: String? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            if (detail != null) {
                Text(
                    detail,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(value, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun GroupPriorityRow(
    group: TrainingGroup,
    priority: GroupPriority?,
    isFocusAvailable: Boolean,
    onPriorityChange: (GroupPriority?) -> Unit
) {
    Column {
        Text(group.displayName, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        val options = listOf<GroupPriority?>(null) + GroupPriority.entries
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, option ->
                SegmentedButton(
                    selected = option == priority,
                    onClick = { onPriorityChange(option) },
                    enabled = option != GroupPriority.FOCUS || isFocusAvailable,
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    icon = {}
                ) {
                    Text(option?.shortName ?: "None", maxLines = 1)
                }
            }
        }
    }
}

@Composable
private fun GroupPrioritiesSummary(priorities: Map<TrainingGroup, GroupPriority>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GroupPriority.entries.forEach { priority ->
                val groups =
                    priorities.filterValues { it == priority }.keys.sortedBy(TrainingGroup::ordinal)
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = priority.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.width(110.dp)
                    )
                    Text(
                        text = groups.joinToString(", ", transform = TrainingGroup::displayName)
                            .ifEmpty { "-" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private val GroupPriority.shortName: String
    get() = when (this) {
        GroupPriority.FOCUS -> "Focus"
        GroupPriority.GROWTH -> "Growth"
        GroupPriority.MAINTENANCE -> "Maint."
    }

private fun Float?.orDash(): String = this?.let(::formatDecimal) ?: "-"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeekCard(
    index: Int,
    week: WeekState,
    weekSessions: SessionStatistics?,
    isEditable: Boolean,
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
                if (isEditable) {
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

            if (weekSessions != null) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))
                WeekStatistics(weekSessions)
            }
        }
    }
}

@Composable
private fun WeekStatistics(sessions: SessionStatistics) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = "Sessions ${sessions.completedSessions}/${sessions.plannedSessions}" +
                        if (sessions.skippedSessions > 0) " (${sessions.skippedSessions} skipped)" else "",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = sessions.adherence?.let(::formatPercent) ?: "-",
                style = MaterialTheme.typography.bodySmall
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = "Time ${formatDuration(sessions.totalDurationMinutes)}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Readiness ${sessions.averageReadiness.orDash()} · Fatigue ${sessions.averageFatigue.orDash()}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
