package gecw.cse.views.common

import gecw.ace.lumina.View
import gecw.ace.lumina.ui.Component

open class Layout(c: Component, active: String):View() {
    init {
        addClass("w-full h-screen overflow-y-auto overflow-x-hidden bg-gray-200 flex")
        add(Sidebar(active))
        add(c)
    }
}