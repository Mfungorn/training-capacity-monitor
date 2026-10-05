package com.fungorn.trainingcapacity.export

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.fungorn.trainingcapacity.core.domain.export.ExportFile
import com.fungorn.trainingcapacity.core.domain.export.FileExporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class AndroidFileExporter(
    private val context: Context
) : FileExporter {

    override suspend fun export(file: ExportFile) {
        val target = withContext(Dispatchers.IO) {
            File(context.cacheDir, EXPORT_DIR).apply { mkdirs() }
                .resolve(file.name)
                .apply { writeText(file.content) }
        }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", target)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = file.mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, file.name)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(shareIntent, file.name).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    companion object {
        const val EXPORT_DIR = "exports"
    }
}
