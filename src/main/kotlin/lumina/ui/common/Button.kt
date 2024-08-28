package gecw.ace.lumina.ui.common

import gecw.ace.lumina.ui.Component

class Button(innerComponent: Component = Component("span")) : Component("button") {
    var enabled = true
        set(value) {
            field = value
            if (value) {
                removeClass("pointer-events-none bg-opacity-50")
            } else {
                addClass("pointer-events-none bg-opacity-50")
            }
        }
    init {
        addClass("bg-black text-lg text-white px-3 py-1 rounded-lg hover:bg-opacity-90 active:bg-opacity-80 transition-all duration-200 ease-in-out")
        add(innerComponent)
        if (!enabled) {
            addClass("pointer-events-none bg-opacity-50")
        }
    }
}