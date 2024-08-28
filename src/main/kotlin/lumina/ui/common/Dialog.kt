package gecw.cse.lumina.ui.common

import gecw.ace.lumina.Lumina
import gecw.ace.lumina.ui.Component
import gecw.ace.lumina.ui.common.Div
import java.util.*

class Dialog(c: Component) : Div() {

    var show: Boolean = false
        set(value) {
            if (value) Lumina.executeJS("document.getElementById('$id').classList.remove('hidden')")
            else Lumina.executeJS("document.getElementById('$id').classList.add('hidden')")
            field = value
        }

    init {
        id = "dia_${UUID.randomUUID()}"
        cn("w-full transition-all duration-200 h-screen fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center")
        cn("hidden")
        add(c.apply {
            disablePropagation()
        })
        onClick {
            show = false
        }
    }
}