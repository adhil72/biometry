package gecw.cse.lumina.ui.common

import gecw.ace.lumina.Lumina
import gecw.ace.lumina.ui.Component
import gecw.ace.lumina.ui.common.Div
import kotlin.properties.Delegates

open class Input(type: String = "text") : Component("input") {

    var enabled = true
        set(value) {
            field = value
            if (value) Lumina.executeJS("document.getElementById('$id')?.classList.remove('pointer-events-none bg-gray-200')")
            else Lumina.executeJS("document.getElementById('$id')?.classList.add('pointer-events-none bg-gray-200')")
        }


    var placeholder: String by Delegates.observable("") { _, _, new ->
        attributes["placeholder"] = new
    }

    init {
        attributes["type"] = type
        cn("outline-none border-2 rounded-lg border-gray-300 px-3 py-2 transition-all duration-200 focus:border-black")
        if (!enabled) cn("pointer-events-none bg-gray-200")
    }
}