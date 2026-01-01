package com.fungorn.trainingcapacity.feature.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fungorn.trainingcapacity.core.ui.components.LoadingIndicator
import com.fungorn.trainingcapacity.feature.dashboard.component.DashboardComponent
import com.fungorn.trainingcapacity.feature.dashboard.graph.DashboardDifficultyGraph
import com.fungorn.trainingcapacity.feature.dashboard.store.DashboardStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(component: DashboardComponent) {
    val state by component.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Training Capacity") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { component.onAddClick() }
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Entry"
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> LoadingIndicator()
                else -> DashboardContent(
                    modifier = Modifier.fillMaxSize(),
                    state = state,
                )
            }
        }
    }
}

@Composable
private fun DashboardContent(
    state: DashboardStore.State,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .verticalScroll(
                state = rememberScrollState()
            )
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Current training mesocycle: ${state.currentMesocycleName}",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            DashboardDataCard(
                modifier = Modifier
                    .weight(1f),
                title = "${state.currentMesocycleWeekNumber} / ${state.totalMesocycleWeeksCount}",
                subtitle = "Mesocycle weeks",
            )
            Spacer(modifier = Modifier.width(12.dp))
            DashboardDataCard(
                modifier = Modifier
                    .weight(1f),
                title = state.currentMesocycleWeekCapacityRange,
                subtitle = "Current week RIR range",
            )
            Spacer(modifier = Modifier.width(12.dp))
            DashboardDataCard(
                modifier = Modifier
                    .weight(1f),
                title = state.totalMesocycleMaximumCapacitySets.toString(),
                subtitle = "RIRs bank",
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Current training program",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            DashboardDataCard(
                modifier = Modifier
                    .weight(1f),
                title = state.currentProgramName,
                subtitle = "Current program",
            )
            Spacer(modifier = Modifier.width(12.dp))
            DashboardDataCard(
                modifier = Modifier
                    .weight(1f),
                title = "${state.currentTrainingDayNumber} / ${state.totalTrainingDaysCount}",
                subtitle = "Training day",
            )
            Spacer(modifier = Modifier.width(12.dp))
            DashboardDataCard(
                modifier = Modifier
                    .weight(1f),
                title = "${state.spentMaximumCapacitySetsThisWeek} / ${state.totalMaximumCapacitySetsThisWeek}",
                subtitle = "Current week max RIRs",
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Overall difficulty",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (state.graphData.entries.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.padding(24.dp),
                    text = "No difficulty data",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            DashboardDifficultyGraph(
                modifier = Modifier.fillMaxWidth(),
                barData = state.graphData
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DashboardDataCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}
