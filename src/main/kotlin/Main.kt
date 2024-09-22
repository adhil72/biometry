package gecw.cse

import gecw.ace.lumina.Lumina
import gecw.ace.lumina.utils.Resource
import gecw.cse.utils.Terminal
import gecw.cse.utils.extractResource
import gecw.cse.views.connection.Connecting
import javafx.application.Application

class App : Lumina() {
    override fun onViewCreated() {
        super.onViewCreated()
        setView(Connecting())
    }
}

fun main() {
    extractResource("driver","driver")
    extractResource("libScanAPI.so","libScanAPI.so")
    Terminal.executeCommand("chmod +x driver",false)
    Terminal.executeCommand("chmod +x libScanAPI.so",false)
    Application.launch(App::class.java)
}