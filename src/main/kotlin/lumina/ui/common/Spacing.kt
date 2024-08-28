package gecw.ace.lumina.ui.common

class Height(): Div() {
    constructor(height: Int) : this() {
        addClass("h-$height")
    }
    constructor(height: String) : this() {
        addClass("h-$height")
    }
}

class Width(): Div() {
    constructor(width: Int) : this() {
        addClass("w-$width")
    }
    constructor(width: String) : this() {
        addClass("w-$width")
    }
}

