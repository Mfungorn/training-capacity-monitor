package com.fungorn.trainingcapacity

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.fungorn.trainingcapacity.root.RootComponent
import com.fungorn.trainingcapacity.root.RootContent
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(rootComponent: RootComponent) {
    MaterialTheme {
        RootContent(rootComponent)
    }
}