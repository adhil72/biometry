package gecw.ace.lumina

import gecw.ace.lumina.ui.common.Body
import gecw.ace.lumina.ui.common.Html
import gecw.ace.lumina.utils.Resource
import gecw.ace.lumina.utils.WebViewIPC
import gecw.cse.lumina.ui.common.ToastLayout
import javafx.animation.PauseTransition
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.web.WebView
import javafx.stage.Stage
import javafx.util.Duration
import org.w3c.dom.Element
import java.io.File


open class Lumina : Application() {


    override fun start(primaryStage: Stage?) {
        p0 = primaryStage
        onViewCreated()
    }

    companion object {
        var p0: Stage? = null
        var pList = ArrayList<Scene>()

        var currentWebView:WebView? = null

        private fun createBody(): Body {
            return Body().apply { add(ToastLayout()) }
        }

        fun setView(view: View) {
            val v0 = Html()
            v0.add(createBody().apply { add(view) })
            currentWebView = WebView().apply { engine.loadContent(v0.render());WebViewIPC(engine) }
            p0?.scene = Scene(currentWebView)
            if (p0?.isShowing == false) p0?.show()
            wait(1) {
                view.onCreated()
            }
            File("index.html").writeText(v0.render().replace(Resource.getAsString("tailwind.js"),"  <script src=\"https://cdn.tailwindcss.com\"></script>"))
        }

        fun pushView(view: View) {
            if (p0?.scene != null && p0?.scene != null) pList.add(p0?.scene!!)
            val v0 = Html()
            v0.add(createBody().apply { add(view) })
            currentWebView = WebView().apply { engine.loadContent(v0.render());WebViewIPC(engine) }
            p0?.scene = Scene(currentWebView)
            if (p0?.isShowing == false) p0?.show()
            wait(1) {
                view.onCreated()
            }
            File("index.html").writeText(v0.render().replace(Resource.getAsString("tailwind.js"),"  <script src=\"https://cdn.tailwindcss.com\"></script>"))
        }

        fun popView() {
            if (pList.isNotEmpty()) {
                p0?.scene = pList.last()
                pList.removeAt(pList.size - 1)
            }
        }

        fun wait(seconds: Int, action: () -> Unit) {
            wait(seconds.toDouble(),action)
        }

        fun wait(seconds: Double, action: () -> Unit) {
            val delay = PauseTransition(Duration.seconds(seconds))
            delay.setOnFinished {
                action()
            }
            delay.play()
        }

        fun element(id:String): Element {
            return currentWebView?.engine?.document?.getElementById(id)!!
        }

        fun executeJS(js:String){
            currentWebView?.engine?.executeScript(js)
        }

        fun interval(seconds: Double, action: () -> Boolean) {
            val delay = PauseTransition(Duration.seconds(seconds.toDouble()))
            delay.setOnFinished {
                if (action()) {
                    interval(seconds, action)
                }
            }
            delay.play()
        }

    }

    open fun onViewCreated() {}
}

