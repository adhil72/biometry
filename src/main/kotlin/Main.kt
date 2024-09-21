package gecw.cse

import gecw.ace.lumina.Lumina
import gecw.cse.views.connection.Connecting
import javafx.application.Application

class App: Lumina(){
    override fun onViewCreated() {
        super.onViewCreated()
        setView(Connecting())
    }
}

fun main() {
   Application.launch(App::class.java)
    println("stated")
}