package com.fungorn.trainingcapacity.export

import com.fungorn.trainingcapacity.core.domain.export.ExportFile
import com.fungorn.trainingcapacity.core.domain.export.FileExporter
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSString
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.writeToFile
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene
import platform.UIKit.popoverPresentationController

class IosFileExporter : FileExporter {

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    override suspend fun export(file: ExportFile) {
        val path = NSTemporaryDirectory() + file.name
        val written = NSString.create(string = file.content)
            .writeToFile(path, atomically = true, encoding = NSUTF8StringEncoding, error = null)
        if (!written) throw IllegalStateException("Failed to write ${file.name}")

        withContext(Dispatchers.Main) {
            val presenter = topViewController()
                ?: throw IllegalStateException("No view controller to present from")
            val controller = UIActivityViewController(
                activityItems = listOf(NSURL.fileURLWithPath(path)),
                applicationActivities = null
            )
            controller.popoverPresentationController?.sourceView = presenter.view
            presenter.presentViewController(controller, animated = true, completion = null)
        }
    }

    private fun topViewController(): UIViewController? {
        val window = UIApplication.sharedApplication.connectedScenes
            .filterIsInstance<UIWindowScene>()
            .flatMap { scene -> scene.windows.filterIsInstance<UIWindow>() }
            .firstOrNull(UIWindow::isKeyWindow)
            ?: return null
        var controller = window.rootViewController
        while (controller?.presentedViewController != null) {
            controller = controller.presentedViewController
        }
        return controller
    }
}
