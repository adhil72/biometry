package gecw.ace.lumina.ui

import gecw.ace.lumina.Lumina
import gecw.ace.lumina.utils.WebViewIPC
import gecw.ace.lumina.utils.generateRandomUid
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.parser.Parser

open class Component(var name: String, var valueVar: Boolean? = false) {
    val attributes = mutableMapOf<String, String>()
    val children = mutableListOf<Component>()
    val classList = mutableListOf<String>().apply { add("select-none") }
    val style = mutableMapOf<String, String>()
    var id: String = name+"_"+generateRandomUid()
    var rendered = false

    companion object {
        fun parseHtml(html: String): Component {
            val document = Jsoup.parse(html, "", Parser.xmlParser())
            return parseElement(document.body())
        }

        private fun parseElement(element: Element): Component {
            val component = Component(name = element.tagName())

            element.attributes().forEach { attr ->
                component.setAttribute(attr.key, attr.value)
            }

            val style = element.attr("style")
            if (style.isNotEmpty()) {
                style.split(";").forEach { styleEntry ->
                    val (key, value) = styleEntry.split(":", limit = 2).map { it.trim() }
                    component.setStyle(key, value)
                }
            }

            val classes = element.classNames()
            classes.forEach { className ->
                component.addClass(className)
            }

            element.children().forEach { childElement ->
                val childComponent = parseElement(childElement)
                component.add(childComponent)
            }

            val text = element.ownText()
            if (text.isNotEmpty()) {
                component.valueVar = true
                component.name = text
            }

            return component
        }
    }

    fun setTagName(name: String) {
        this.name = name
    }

    fun addClass(className: String) {
        if (rendered) Lumina.executeJS("document.getElementById('$id').classList.add('$className')")
        else classList.add(className)
    }

    fun removeClass(className: String) {
        if (rendered) Lumina.executeJS("document.getElementById('$id').classList.remove('$className')")
        else classList.remove(className)
    }

    fun toggleClass(className: String) {
        if (classList.contains(className)) {
            classList.remove(className)
        } else {
            classList.add(className)
        }
    }

    fun setAttribute(key: String, value: String) {
        if (rendered) Lumina.executeJS("document.getElementById('$id').setAttribute('$key', '$value')")
        else attributes[key] = value
    }

    fun removeAttribute(key: String) {
        attributes.remove(key)
    }

    fun setStyle(key: String, value: String) {
        style[key] = value
    }

    fun removeStyle(key: String) {
        style.remove(key)
    }

    fun add(child: Component, className: String = "") {
        child.classList.add(className)
        if (rendered) Lumina.executeJS("document.getElementById('$id').innerHTML += `${child.render()}`")
        else children.add(child)

    }

    fun add(child: String) {
        if (rendered) Lumina.executeJS("document.getElementById('$id').innerHTML += `$child`")
        else children.add(Component(child, valueVar = true))
    }

    fun remove(child: Component) {
        if (rendered) Lumina.executeJS("document.getElementById('$id').removeChild(document.getElementById('${child.id}'))")
        else children.remove(child)
    }

    fun render(): String {
        rendered = true
        if (valueVar == true) return name
        val attributesString = attributes.map { (key, value) -> "$key=\"$value\"" }.joinToString(" ")
        val classListString = if (classList.isNotEmpty()) "class=\"${classList.joinToString(" ")}\"" else ""
        val styleString = if (style.isNotEmpty()) "style=\"${
            style.map { (key, value) -> "$key: $value" }.joinToString("; ")
        }\"" else ""
        val childrenString = children.joinToString("") { it.render() }
        return """<$name id="$id" $attributesString $classListString $styleString>$childrenString</$name>"""
    }

    fun cn(string: String) {
        if (rendered) Lumina.executeJS("document.getElementById('$id').classList.add('$string')")
        else classList.add(string)
    }

    fun onClick(res: () -> Unit) {
        if (rendered) Lumina.executeJS("document.getElementById('$id').onclick = () => clickHandler('$id')")
        else attributes["onclick"] = "clickHandler('$id')"
        WebViewIPC.clickListeners[id] = res

    }

    fun onFormSubmit(res: () -> Unit) {
        WebViewIPC.formSubmitListeners[id] = res
        if (rendered) Lumina.executeJS("document.getElementById('$id').onsubmit = () => formSubmitHandler('$id')")
        else attributes["onsubmit"] = "formSubmitHandler('$id')"

    }

    fun onMouseEnter(res: () -> Unit) {
        attributes["onmouseenter"] = "mouseEnterHandler('$id')"
        WebViewIPC.onMouseEnterListeners[id] = res
    }

    fun onMouseLeave(res: () -> Unit) {
        attributes["onmouseleave"] = "mouseLeaveHandler('$id')"
        WebViewIPC.onMouseLeaveListeners[id] = res
    }

    fun onChange(res: () -> Unit) {
        if (rendered) Lumina.executeJS("document.getElementById('$id').onchange = () => changeHandler('$id')")
        else attributes["onchange"] = "changeHandler('$id')"
        WebViewIPC.onChangeListeners[id] = res
    }

    fun onInput(res: () -> Unit) {
        attributes["oninput"] = "inputHandler('$id')"
        WebViewIPC.onInputListeners[id] = res
    }

    fun onScroll(res: () -> Unit) {
        attributes["onscroll"] = "scrollHandler('$id')"
        WebViewIPC.onScrollListeners[id] = res
    }

    fun onKeyPress(res: () -> Unit) {
        attributes["onkeypress"] = "keyPressHandler('$id')"
        WebViewIPC.onKeyPressListeners[id] = res
    }

    fun onKeyUp(res: () -> Unit) {
        attributes["onkeyup"] = "keyUpHandler('$id')"
        WebViewIPC.onKeyUpListeners[id] = res
    }

    fun onKeyDown(res: () -> Unit) {
        attributes["onkeydown"] = "keyDownHandler('$id')"
        WebViewIPC.onKeyDownListeners[id] = res
    }

    fun onContextMenu(res: () -> Unit) {
        attributes["oncontextmenu"] = "contextMenuHandler('$id')"
        WebViewIPC.onContextMenuListeners[id] = res
    }

    fun onDoubleClick(res: () -> Unit) {
        attributes["ondblclick"] = "doubleClickHandler('$id')"
        WebViewIPC.onDoubleClickListeners[id] = res
    }

    fun onDrag(res: () -> Unit) {
        attributes["ondrag"] = "dragHandler('$id')"
        WebViewIPC.onDragListeners[id] = res
    }

    fun onDragEnd(res: () -> Unit) {
        attributes["ondragend"] = "dragEndHandler('$id')"
        WebViewIPC.onDragEndListeners[id] = res
    }

    fun onDragEnter(res: () -> Unit) {
        attributes["ondragenter"] = "dragEnterHandler('$id')"
        WebViewIPC.onDragEnterListeners[id] = res
    }

    fun onDragExit(res: () -> Unit) {
        attributes["ondragexit"] = "dragExitHandler('$id')"
        WebViewIPC.onDragExitListeners[id] = res
    }

    fun onDragLeave(res: () -> Unit) {
        attributes["ondragleave"] = "dragLeaveHandler('$id')"
        WebViewIPC.onDragLeaveListeners[id] = res
    }

    fun onDragOver(res: () -> Unit) {
        attributes["ondragover"] = "dragOverHandler('$id')"
        WebViewIPC.onDragOverListeners[id] = res
    }

    fun onDragStart(res: () -> Unit) {
        attributes["ondragstart"] = "dragStartHandler('$id')"
        WebViewIPC.onDragStartListeners[id] = res
    }

    fun onDrop(res: () -> Unit) {
        attributes["ondrop"] = "dropHandler('$id')"
        WebViewIPC.onDropListeners[id] = res
    }

    fun onFocus(res: () -> Unit) {
        attributes["onfocus"] = "focusHandler('$id')"
        WebViewIPC.onFocusListeners[id] = res
    }

    fun onBlur(res: () -> Unit) {
        attributes["onblur"] = "blurHandler('$id')"
        WebViewIPC.onBlurListeners[id] = res
        if (rendered) Lumina.executeJS("document.getElementById('$id').onblur = () => blurHandler('$id')")
    }

    fun flex() {
        classList.add("flex")
    }

    fun flexRow() {
        classList.add("flex-row")
    }

    fun flexCol() {
        classList.add("flex-col")
    }

    fun flexWrap() {
        classList.add("flex-wrap")
    }

    fun flex1() {
        classList.add("flex-1")
    }

    fun justifyCenter() {
        classList.add("justify-center")
    }

    fun justifyStart() {
        classList.add("justify-start")
    }

    fun justifyEnd() {
        classList.add("justify-end")
    }

    fun justifyBetween() {
        classList.add("justify-between")
    }

    fun justifyAround() {
        classList.add("justify-around")
    }

    fun justifyEvenly() {
        classList.add("justify-evenly")
    }

    fun itemsCenter() {
        classList.add("items-center")
    }

    fun itemsStart() {
        classList.add("items-start")
    }

    fun itemsEnd() {
        classList.add("items-end")
    }

    fun itemsBaseline() {
        classList.add("items-baseline")
    }

    fun itemsStretch() {
        classList.add("items-stretch")
    }

    fun textCenter() {
        classList.add("text-center")
    }

    fun textLeft() {
        classList.add("text-left")
    }

    fun textRight() {
        classList.add("text-right")
    }

    fun textJustify() {
        classList.add("text-justify")
    }

    fun fontSemiBold() {
        classList.add("font-semibold")
    }

    fun fontBold() {
        classList.add("font-bold")
    }

    fun extraBold() {
        classList.add("extra-bold")
    }

    fun fontNormal() {
        classList.add("font-normal")
    }

    fun color(color: String) {
        classList.add("text-$color")
    }

    fun bg(color: String) {
        classList.add("bg-$color")
    }

    fun bgTransparent() {
        classList.add("bg-transparent")
    }

    fun bgWhite() {
        classList.add("bg-white")
    }

    fun bgBlack() {
        classList.add("bg-black")
    }

    fun textWhite() {
        classList.add("text-white")
    }

    fun textBlack() {
        classList.add("text-black")
    }

    fun textGray() {
        classList.add("text-gray-500")
    }

    fun textGrayDark() {
        classList.add("text-gray-700")
    }

    fun disablePropagation() {
        attributes["onclick"] = "event.stopPropagation()"
    }

    fun removeChilds() {
        children.clear()
        if (rendered) Lumina.executeJS("document.getElementById('$id').innerHTML = ''")
    }

    fun removeChilds(start:Int,end:Int){
        if (rendered) {
            val ids = children.subList(start,end)
            ids.forEach{
                Lumina.executeJS("""
                    document.getElementById('${it.id}').remove()
                """.trimIndent())
            }
        }
        children.removeAll(children.subList(start,end))
    }
}
