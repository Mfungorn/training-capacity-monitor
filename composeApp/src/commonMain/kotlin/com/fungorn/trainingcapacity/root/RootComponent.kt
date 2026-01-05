package com.fungorn.trainingcapacity.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.fungorn.trainingcapacity.feature.dashboard.presentation.component.DashboardComponent
import com.fungorn.trainingcapacity.feature.form.presentation.component.FormComponent
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.component.MesocycleDetailComponent
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.component.MesocyclesComponent
import com.fungorn.trainingcapacity.feature.programs.presentation.component.ProgramsComponent

interface RootComponent {
    
    val childStack: Value<ChildStack<*, Child>>
    
    sealed interface Child {
        data class Dashboard(val component: DashboardComponent) : Child
        data class Form(val component: FormComponent) : Child
        data class Mesocycles(val component: MesocyclesComponent) : Child
        data class MesocycleDetail(val component: MesocycleDetailComponent) : Child
        data class Programs(val component: ProgramsComponent) : Child
    }
}
