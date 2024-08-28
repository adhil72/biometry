package gecw.cse.views.common

import gecw.ace.lumina.Lumina
import gecw.ace.lumina.View
import gecw.ace.lumina.ui.Component
import gecw.ace.lumina.ui.common.Div
import gecw.ace.lumina.ui.common.Image
import gecw.ace.lumina.ui.common.Span
import gecw.ace.lumina.utils.Resource
import gecw.cse.views.home.CreateUserView
import gecw.cse.views.home.Home

fun sidebarItem(name: String, icon: String, active: Boolean = false): Component {
    return Div().apply {
        onClick {
            if (name == "Dashboard") {
                Lumina.setView(Home())
            }else{
                Lumina.setView(CreateUserView())
            }
        }
        cn("flex text-gray-600 items-center w-full cursor-pointer rounded-md p-2 transition-all duration-200 ease-in-out")
        add(Image(icon), "w-5 h-5")
        add(Span(name), "ml-3")
        if (active) {
            cn("bg-gray-300")
        } else {
            cn("hover:bg-gray-100 active:bg-gray-300 bg-white")
        }
    }
}

val title = Span("Biometry Admin").apply { cn("text-2xl font-bold text-black") }

class Sidebar(active: String) : View() {
    init {
        addClass("w-64 h-screen bg-white shadow-lg flex flex-col p-5")
        add(title)
        add(sidebarItem("Dashboard", Resource.icon("home.svg"), active == "home"), "mt-5")
        add(sidebarItem("Create student", Resource.icon("user_add.svg"), active == "create"), "mt-1")
        add(sidebarItem("Record attendance", Resource.icon("finger.svg"), active == "record"), "mt-1")
        add(sidebarItem("View Records", Resource.icon("table.svg"), active == "view"), "mt-1")
    }
}