package gecw.cse.views.connection

import gecw.ace.lumina.Lumina
import gecw.ace.lumina.View
import gecw.ace.lumina.ui.common.Span
import gecw.cse.http.services.testConnectionService
import gecw.cse.views.home.Home
import javafx.application.Platform

class Connecting : View() {

    init {
        cn("w-full h-screen flex justify-center items-center")
        add(Span("Connecting").apply {
            cn("text-4xl")
        })
    }

    override fun onCreated() {
        super.onCreated()

        Thread {
            var connected = false
            while (true) {
                if (connected) break
                testConnectionService(onSuccess = {
                    connected = true
                    Platform.runLater { Lumina.setView(Home()) }
                }, onFailure = {
                    println("sl started")
                    Thread.sleep(3000)
                    println("sl stoped")
                })
            }
        }.start()
    }
}