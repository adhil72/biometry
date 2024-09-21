package gecw.cse.lumina.ui.common

import gecw.ace.lumina.Lumina
import gecw.ace.lumina.ui.Component
import kotlin.properties.Delegates

open class Input(type: String = "text") : Component("input") {

    var value: String
        get() = Lumina.executeJS("document.getElementById('$id').value") ?: ""
        set(value) {
            Lumina.executeJS("document.getElementById('$id').value = '$value'")
        }

    var enabled = true
        set(value) {
            field = value
            if (value) Lumina.executeJS("document.getElementById('$id')?.classList.remove('pointer-events-none bg-gray-200')")
            else Lumina.executeJS("document.getElementById('$id')?.classList.add('pointer-events-none bg-gray-200')")
        }


    var placeholder: String by Delegates.observable("") { _, _, new ->
        attributes["placeholder"] = new
    }

    var type: String by Delegates.observable(type) { _, _, new ->
        setAttribute("type", new)
    }

    init {
        setAttribute("type", type)
        cn("outline-none border-2 rounded-lg border-gray-300 px-3 py-2 transition-all duration-200 focus:border-black")
        if (!enabled) cn("pointer-events-none bg-gray-200")
        if (type == "date") {
            setAttribute("type", "text")
        }
    }
}