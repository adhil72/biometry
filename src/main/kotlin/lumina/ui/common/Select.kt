package gecw.cse.lumina.ui.common

import gecw.ace.lumina.Lumina
import gecw.ace.lumina.ui.Component

open class Select : Component("select") {
    var value: String
        get() {
            return if (rendered) {
                Lumina.executeJS("document.getElementById('$id')?.value")
            } else {
                ""
            }
        }
        set(value) {
            Lumina.executeJS("document.getElementById('$id').value = '$value'")
        }

    init {
        cn("outline-none rounded-lg border border-gray-300 px-3 py-2")
    }
}

open class Option(name: String, value: String) : Component("option") {

    var value: String
        get() = attributes["value"]!!
        set(value) {
            attributes["value"] = value
        }
    var text: String
        get() = children[0].name
        set(value) {
            add(Component(value, valueVar = true))
        }

    init {
        this.value = value
        this.text = name

        cn("w-full")
    }
}