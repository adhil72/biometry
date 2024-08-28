package gecw.cse.lumina.ui.common

import gecw.ace.lumina.ui.Component
import gecw.ace.lumina.utils.Resource
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import java.io.File

fun childrenDocumentToComponent(document: org.jsoup.nodes.Document): Component {
    val c = Component(document.children()[0].tagName())
    val attributes = document.children()[0].attributes()
    for (attr in attributes.asList()){
        val attrName = attr.toString().split("=")[0]
        val attrValue = attr.toString().split("=")[1].replace("\"", "")
        if (attrName!="width" && attrName!="height"){
            if (attrName=="class"){
                c.cn(attrValue)
                continue
            }
            c.setAttribute(attrName, attrValue)
        }
    }
    return c
}

fun nestedDocumentToComponent(document: org.jsoup.nodes.Document): Component {
    val c = childrenDocumentToComponent(document)

    for (child in document.children()[0].children()){
        val childComponent = childrenDocumentToComponent(Jsoup.parse(child.outerHtml(), "", Parser.xmlParser()))
        c.add(childComponent)
    }

    return c
}

open class Svg(name: String) : Component("", valueVar = true) {

    companion object{
        fun parse(name: String): Component {
            val svg = Resource.getAsString("icons/$name.svg")
            val document = Jsoup.parse(svg, "", Parser.xmlParser())
            val c = nestedDocumentToComponent(document)
            File("test.html").writeText(c.render())
            return c
        }
    }
    init {
        setTagName(Resource.getAsString("icons/$name.svg"))
    }
}