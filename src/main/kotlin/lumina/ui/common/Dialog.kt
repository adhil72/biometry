package gecw.cse.lumina.ui.common

import gecw.ace.lumina.Lumina
import gecw.ace.lumina.ui.Component
import gecw.ace.lumina.ui.common.Div
import java.util.*

interface DialogListener {
    fun onClose()
    fun onManualClose()
}

class Dialog(c: Component, var dialogListener:DialogListener?=null) : Div() {

    var show: Boolean = false
        set(value) {
            if (value) Lumina.executeJS("document.getElementById('$id').classList.remove('hidden')")
            else {
                Lumina.executeJS("document.getElementById('$id').classList.add('hidden')")
                dialogListener?.onClose()
            }
            field = value
        }

    init {
        id = "dia_${UUID.randomUUID()}"
        cn("w-full transition-all duration-200 h-screen fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center")
        if (!show) cn("hidden")
        add(c.apply {
            disablePropagation()
        })
        onClick {
            dialogListener?.onManualClose()
            show = false
        }
    }
}