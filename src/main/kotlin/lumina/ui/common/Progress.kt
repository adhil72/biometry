package gecw.cse.lumina.ui.common

import gecw.ace.lumina.Lumina
import gecw.ace.lumina.ui.common.Div
import javafx.application.Platform
import java.util.UUID

class Progress:Div() {

    val progressTint = Div().apply {
        id="progressTint_${UUID.randomUUID()}"
        cn("h-full bg-black rounded-lg transition-all duration-300 ease-in-out")
        style["width"] = "0%"
    }

    var progressPercentage = 0.0
        set(value) {
            Lumina.executeJS("document.getElementById('${progressTint.id}').style.width = '${value}%'")
            field = value
        }


    init {
        cn("w-full h-3 bg-gray-300 rounded-lg overflow-hidden")
        add(progressTint)
    }
}