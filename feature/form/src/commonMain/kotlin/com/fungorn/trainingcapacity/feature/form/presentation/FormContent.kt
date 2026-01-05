package com.fungorn.trainingcapacity.feature.form.presentation

import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fungorn.trainingcapacity.core.ui.components.LoadingIndicator
import com.fungorn.trainingcapacity.core.ui.components.NumberPicker
import com.fungorn.trainingcapacity.feature.form.presentation.component.FormComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormContent(component: FormComponent) {
    val state by component.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("New Entry")
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
                    .verticalScroll(rememberScrollState())
            ) {
                NumberPicker(
                    label = "Max. Capacity Sets",
                    value = state.maxCapacitySets,
                    onValueChange = { component.onMaxCapacitySetsChange(it) },
                    minValue = 0,
                    maxValue = 99
                )
                Spacer(modifier = Modifier.height(32.dp))
                NumberPicker(
                    label = "Overall Difficulty",
                    value = state.overallDifficulty,
                    onValueChange = { component.onOverallDifficultyChange(it) },
                    minValue = 0,
                    maxValue = 3
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { component.onSaveClick() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
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
