package com.fungorn.trainingcapacity.root

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.fungorn.trainingcapacity.feature.dashboard.DashboardContent
import com.fungorn.trainingcapacity.feature.form.FormContent

@Composable
fun RootContent(component: RootComponent) {
    Children(
        stack = component.childStack,
        animation = stackAnimation(fade() + scale())
    ) { child ->
        when (val instance = child.instance) {
            is RootComponent.Child.Dashboard -> DashboardContent(instance.component)
            is RootComponent.Child.Form -> FormContent(instance.component)
        }
    }
}
