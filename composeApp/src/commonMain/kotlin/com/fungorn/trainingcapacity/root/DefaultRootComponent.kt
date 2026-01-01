package com.fungorn.trainingcapacity.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.fungorn.trainingcapacity.core.domain.repository.TrainingRepository
import com.fungorn.trainingcapacity.core.domain.usecase.AddEntryUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetActiveMesocycleUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetAllEntriesUseCase
import com.fungorn.trainingcapacity.feature.dashboard.component.DashboardComponent
import com.fungorn.trainingcapacity.feature.dashboard.component.DefaultDashboardComponent
import com.fungorn.trainingcapacity.feature.form.component.DefaultFormComponent
import com.fungorn.trainingcapacity.feature.form.component.FormComponent
import com.fungorn.trainingcapacity.root.DefaultRootComponent.Config.Form
import kotlinx.serialization.Serializable

@OptIn(com.arkivanov.decompose.DelicateDecomposeApi::class)
class DefaultRootComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val getActiveMesocycleUseCase: GetActiveMesocycleUseCase,
    private val getAllEntriesUseCase: GetAllEntriesUseCase,
    private val addEntryUseCase: AddEntryUseCase,
    private val repository: TrainingRepository
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val childStack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Dashboard,
            handleBackButton = true,
            childFactory = ::createChild
        )

    private fun createChild(
        config: Config,
        componentContext: ComponentContext
    ): RootComponent.Child =
        when (config) {
            is Config.Dashboard -> RootComponent.Child.Dashboard(
                createDashboardComponent(componentContext)
            )

            is Config.Form -> RootComponent.Child.Form(
                createFormComponent(componentContext, config.entryId)
            )
        }

    private fun createDashboardComponent(componentContext: ComponentContext): DashboardComponent =
        DefaultDashboardComponent(
            componentContext = componentContext,
            storeFactory = storeFactory,
            getActiveMesocycleUseCase = getActiveMesocycleUseCase,
            getAllEntriesUseCase = getAllEntriesUseCase,
            onOutput = ::onDashboardOutput
        )

    private fun createFormComponent(
        componentContext: ComponentContext,
        entryId: String?
    ): FormComponent =
        DefaultFormComponent(
            componentContext = componentContext,
            storeFactory = storeFactory,
            addEntryUseCase = addEntryUseCase,
            repository = repository,
            entryId = entryId,
            onOutput = ::onFormOutput
        )

    private fun onDashboardOutput(output: DashboardComponent.Output) {
        when (output) {
            DashboardComponent.Output.NavigateToMesocycles -> {
                // TODO
            }

            DashboardComponent.Output.NavigateToPrograms -> {
                // TODO
            }

            is DashboardComponent.Output.NavigateToForm -> navigation.push(Form(null))
        }
    }

    private fun onFormOutput(output: FormComponent.Output) {
        when (output) {
            is FormComponent.Output.NavigateBack -> navigation.pop()
        }
    }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Dashboard : Config

        @Serializable
        data class Form(val entryId: String?) : Config
    }
}
