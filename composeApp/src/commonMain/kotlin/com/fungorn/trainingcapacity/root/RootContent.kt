package com.fungorn.trainingcapacity.root

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.fungorn.trainingcapacity.feature.dashboard.presentation.DashboardContent
import com.fungorn.trainingcapacity.feature.form.presentation.FormContent
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.MesocycleDetailContent
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.MesocyclesContent
import com.fungorn.trainingcapacity.feature.programs.presentation.ProgramDetailContent
import com.fungorn.trainingcapacity.feature.programs.presentation.ProgramsContent

@Composable
fun RootContent(component: RootComponent) {
    Children(
        stack = component.childStack,
        animation = stackAnimation(fade() + scale())
    ) { child ->
        when (val instance = child.instance) {
            is RootComponent.Child.Dashboard -> DashboardContent(instance.component)
            is RootComponent.Child.Form -> FormContent(instance.component)
            is RootComponent.Child.Mesocycles -> MesocyclesContent(instance.component)
            is RootComponent.Child.MesocycleDetail -> MesocycleDetailContent(instance.component)
            is RootComponent.Child.Programs -> ProgramsContent(instance.component)
            is RootComponent.Child.ProgramDetail -> ProgramDetailContent(instance.component)
        }
    }
}
