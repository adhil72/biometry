package gecw.cse.lumina.ui.common

import gecw.ace.lumina.ui.Component

class Table:Component("table"){
    fun clear(){
        removeChilds(1,children.size)
    }
}
class Tr:Component("tr")
class Th(title:String):Component("th"){
    init {
        cn("p-3 text-center border")
        add(title)
    }
}
class Td(data:String):Component("td"){
    init {
        cn("p-3 text-center border")
        add(data)
    }
}