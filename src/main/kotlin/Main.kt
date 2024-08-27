package gecw.cse

import gecw.ace.db.MongoDbManager
import gecw.ace.webserver.Webserver
import gecw.cse.fx.Window
import gecw.cse.utils.Terminal
import gecw.cse.utils.extractResource
import gecw.cse.utils.waitForPortOpen
import javafx.application.Platform
import javafx.stage.Stage
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext
import java.io.File

class Main {
    companion object{
        var springApplication:ConfigurableApplicationContext?=null
    }
}
fun main() {

    extractResource("libScanAPI.so", File("libScanAPI.so").absolutePath)
    extractResource("driver", File("driver").absolutePath)

    Terminal.executeCommand("chmod +x driver",false,false)
    Terminal.executeCommand("chmod +x libScanAPI.so",false,false)

    File("fingerprints").mkdirs()
    File("prints").mkdirs()
    File("temps").mkdirs()

    MongoDbManager.connect()
    MongoDbManager.mongoClient.getDatabase("ace").getCollection("users").find().forEach {
        println(it)
    }
    Main.springApplication=SpringApplication.run(Webserver::class.java)
    waitForPortOpen(8080)
    Platform.startup {
        Window().start(Stage())
    }
}