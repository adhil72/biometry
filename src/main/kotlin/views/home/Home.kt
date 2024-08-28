package gecw.cse.views.home

import gecw.ace.lumina.ui.common.Div
import gecw.ace.lumina.ui.common.Span
import gecw.cse.views.common.Layout

val homeComponent = Div().apply {
    addClass("flex-1 flex flex-col h-screen px-10 py-5")
    add(Span("Dashboard"),"text-2xl font-bold text-black")
}

class Home:Layout(homeComponent,"home") {
    override fun onCreated() {
        super.onCreated()

    }
}