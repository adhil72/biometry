package gecw.ace.lumina.ui.common

import gecw.ace.lumina.ui.Component

class Image(src: String = "") : Component("img") {
    init {
        attributes["src"] = src
    }
}