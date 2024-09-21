package gecw.ace.lumina.ui.common

import gecw.ace.lumina.Lumina
import gecw.ace.lumina.ui.Component

class Span(var text: String? = null) : Component("span") {
    var value: String
        get() = Lumina.element(id).textContent ?: ""
        set(value) {
            Lumina.element(id).textContent = value
        }
    init {
        text?.let { add(it) }
        cn("inherit-cursor")
    }
}