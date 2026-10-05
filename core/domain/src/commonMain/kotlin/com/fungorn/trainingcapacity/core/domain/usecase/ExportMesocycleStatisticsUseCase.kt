package com.fungorn.trainingcapacity.core.domain.usecase

import com.fungorn.trainingcapacity.core.domain.export.FileExporter
import com.fungorn.trainingcapacity.core.domain.export.MesocycleCsvExporter
import kotlinx.coroutines.flow.firstOrNull

class ExportMesocycleStatisticsUseCase(
    private val getMesocycleStatisticsUseCase: GetMesocycleStatisticsUseCase,
    private val csvExporter: MesocycleCsvExporter,
    private val fileExporter: FileExporter
) {
    suspend operator fun invoke(mesocycleId: String) {
        val statistics = getMesocycleStatisticsUseCase(mesocycleId).firstOrNull()
            ?: throw IllegalStateException("Mesocycle not found")
        fileExporter.export(csvExporter.toExportFile(statistics))
    }
}
