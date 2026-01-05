package com.fungorn.trainingcapacity.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.fungorn.trainingcapacity.core.domain.repository.MesocycleRepository
import com.fungorn.trainingcapacity.core.domain.usecase.AddEntryUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.GetEntryByIdUseCase
import com.fungorn.trainingcapacity.core.domain.usecase.StartNewMesocycleUseCase
import com.fungorn.trainingcapacity.feature.dashboard.domain.usecase.GetDashboardDataUseCase
import com.fungorn.trainingcapacity.feature.dashboard.presentation.component.DashboardComponent
import com.fungorn.trainingcapacity.feature.dashboard.presentation.component.DefaultDashboardComponent
import com.fungorn.trainingcapacity.feature.form.domain.usecase.GetCurrentTrainingContextUseCase
import com.fungorn.trainingcapacity.feature.form.presentation.component.DefaultFormComponent
import com.fungorn.trainingcapacity.feature.form.presentation.component.FormComponent
import com.fungorn.trainingcapacity.feature.mesocycles.domain.usecase.GetMesocycleListUseCase
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.component.DefaultMesocycleDetailComponent
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.component.DefaultMesocyclesComponent
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.component.MesocycleDetailComponent
import com.fungorn.trainingcapacity.feature.mesocycles.presentation.component.MesocyclesComponent
import com.fungorn.trainingcapacity.feature.programs.presentation.component.DefaultProgramsComponent
import com.fungorn.trainingcapacity.feature.programs.presentation.component.ProgramsComponent
import kotlinx.serialization.Serializable

@OptIn(com.arkivanov.decompose.DelicateDecomposeApi::class)
class DefaultRootComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val getDashboardDataUseCase: GetDashboardDataUseCase,
    private val addEntryUseCase: AddEntryUseCase,
    private val getEntryByIdUseCase: GetEntryByIdUseCase,
    private val getCurrentTrainingContextUseCase: GetCurrentTrainingContextUseCase,
    private val getMesocycleListUseCase: GetMesocycleListUseCase,
    private val mesocycleRepository: MesocycleRepository,
    private val startNewMesocycleUseCase: StartNewMesocycleUseCase
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

            is Config.Mesocycles -> RootComponent.Child.Mesocycles(
                createMesocyclesComponent(componentContext)
            )

            is Config.MesocycleDetail -> RootComponent.Child.MesocycleDetail(
                createMesocycleDetailComponent(componentContext, config.mesocycleId)
            )

            is Config.Programs -> RootComponent.Child.Programs(
                createProgramsComponent(componentContext)
            )
        }

    private fun createDashboardComponent(componentContext: ComponentContext): DashboardComponent =
        DefaultDashboardComponent(
            componentContext = componentContext,
            storeFactory = storeFactory,
            getDashboardDataUseCase = getDashboardDataUseCase,
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
            getEntryByIdUseCase = getEntryByIdUseCase,
            getCurrentTrainingContextUseCase = getCurrentTrainingContextUseCase,
            entryId = entryId,
            onOutput = ::onFormOutput
        )

    private fun createMesocyclesComponent(componentContext: ComponentContext): MesocyclesComponent =
        DefaultMesocyclesComponent(
            componentContext = componentContext,
            storeFactory = storeFactory,
            getMesocycleListUseCase = getMesocycleListUseCase,
            onOutput = ::onMesocyclesOutput
        )

    private fun createMesocycleDetailComponent(
        componentContext: ComponentContext,
        mesocycleId: String?
    ): MesocycleDetailComponent =
        DefaultMesocycleDetailComponent(
            componentContext = componentContext,
            storeFactory = storeFactory,
            mesocycleRepository = mesocycleRepository,
            startNewMesocycleUseCase = startNewMesocycleUseCase,
            mesocycleId = mesocycleId,
            onOutput = ::onMesocycleDetailOutput
        )

    private fun createProgramsComponent(componentContext: ComponentContext): ProgramsComponent =
        DefaultProgramsComponent(
            componentContext = componentContext,
            storeFactory = storeFactory,
            onOutput = ::onProgramsOutput
        )

    private fun onDashboardOutput(output: DashboardComponent.Output) {
        when (output) {
            DashboardComponent.Output.NavigateToMesocycles ->
                navigation.push(Config.Mesocycles)

            DashboardComponent.Output.NavigateToPrograms ->
                navigation.push(Config.Programs)

            is DashboardComponent.Output.NavigateToForm ->
                navigation.push(Config.Form(null))
        }
    }

    private fun onFormOutput(output: FormComponent.Output) {
        when (output) {
            is FormComponent.Output.NavigateBack -> navigation.pop()
        }
    }

    private fun onMesocyclesOutput(output: MesocyclesComponent.Output) {
        when (output) {
            MesocyclesComponent.Output.NavigateBack -> navigation.pop()
            MesocyclesComponent.Output.NavigateToCreateMesocycle -> navigation.push(
                Config.MesocycleDetail(
                    null
                )
            )

            is MesocyclesComponent.Output.NavigateToViewMesocycle -> navigation.push(
                Config.MesocycleDetail(
                    output.id
                )
            )
        }
    }

    private fun onMesocycleDetailOutput(output: MesocycleDetailComponent.Output) {
        when (output) {
            MesocycleDetailComponent.Output.NavigateBack -> navigation.pop()
        }
    }

    private fun onProgramsOutput(output: ProgramsComponent.Output) {
        when (output) {
            ProgramsComponent.Output.NavigateBack -> navigation.pop()
        }
    }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Dashboard : Config

        @Serializable
        data class Form(val entryId: String?) : Config

        @Serializable
        data object Mesocycles : Config

        @Serializable
        data class MesocycleDetail(val mesocycleId: String?) : Config

        @Serializable
        data object Programs : Config
    }
}
