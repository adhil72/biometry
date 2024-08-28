package gecw.cse

import gecw.ace.lumina.Lumina
import gecw.cse.views.home.CreateUserView
import gecw.cse.views.home.Home
import javafx.application.Application
import java.io.File

class App: Lumina(){
    override fun onViewCreated() {
        super.onViewCreated()
        setView(CreateUserView())
    }
}

fun main() {
   Application.launch(App::class.java)
}