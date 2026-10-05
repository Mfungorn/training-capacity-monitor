package com.fungorn.trainingcapacity.core.domain.export

interface FileExporter {
    suspend fun export(file: ExportFile)
}

data class ExportFile(
    val name: String,
    val mimeType: String,
    val content: String,
)
