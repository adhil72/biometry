package gecw.cse.fx

import gecw.cse.Main
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.web.WebView
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.channels.Channels
import java.nio.channels.ReadableByteChannel
import java.nio.channels.WritableByteChannel

class Window:Application() {

    override fun stop() {
        super.stop()
        Main.springApplication?.stop()
    }

    override fun start(primaryStage: Stage) {
        val webView = WebView()
        webView.engine.load("http://localhost:8080")
        webView.engine.locationProperty().addListener {
            _, _, newLocation ->
            if (newLocation.contains("/export/")) {
                downloadFile(newLocation, primaryStage)
            }
        }

        val scene = Scene(webView, 800.0, 600.0)
        primaryStage.scene = scene
        primaryStage.title = "Spring Boot with JavaFX WebView"
        primaryStage.show()
    }

    fun downloadFile(url: String, stage: Stage) {
        val fileChooser = FileChooser()
        fileChooser.title = "Save File"
        fileChooser.initialDirectory = File(System.getProperty("user.home"))

        val file = fileChooser.showSaveDialog(stage) ?: return // Return if no file is selected

        try {
            val urlConnection = URL(url).openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.connect()

            val inputStream: InputStream = urlConnection.inputStream
            val readableByteChannel: ReadableByteChannel = Channels.newChannel(inputStream)
            FileOutputStream(file).use { fileOutputStream ->
                val writableByteChannel: WritableByteChannel = Channels.newChannel(fileOutputStream)
                // Transfer data from the ReadableByteChannel to WritableByteChannel
                val buffer = ByteArray(4096)
                var bytesRead: Int
                while (readableByteChannel.read(java.nio.ByteBuffer.wrap(buffer)).also { bytesRead = it } != -1) {
                    writableByteChannel.write(java.nio.ByteBuffer.wrap(buffer, 0, bytesRead))
                }
            }

            println("Download completed: $file")
        } catch (e: Exception) {
            println("Error downloading file: ${e.message}")
        }
    }


}