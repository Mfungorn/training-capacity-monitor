package com.fungorn.trainingcapacity.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.fungorn.trainingcapacity.feature.dashboard.component.DashboardComponent
import com.fungorn.trainingcapacity.feature.form.component.FormComponent

interface RootComponent {
    
    val childStack: Value<ChildStack<*, Child>>
    
    sealed interface Child {
        data class Dashboard(val component: DashboardComponent) : Child
        data class Form(val component: FormComponent) : Child
    }
}
