package com.fungorn.trainingcapacity

import androidx.compose.runtime.Composable
import com.fungorn.trainingcapacity.root.RootComponent
import com.fungorn.trainingcapacity.root.RootContent
import com.fungorn.trainingcapacity.ui.theme.TrainingCapacityTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(rootComponent: RootComponent) {
    TrainingCapacityTheme {
        RootContent(rootComponent)
    }
}