package gecw.cse.lumina.ui.common

import gecw.ace.lumina.Lumina
import gecw.ace.lumina.ui.Component
import gecw.ace.lumina.ui.common.Div
import gecw.ace.lumina.ui.common.Span
import java.util.UUID


class ToastItem(title:String,icon:Component?=null):Div(){
    init {
        cn("text-black bg-white font-semibold p-2 border rounded-lg shadow-lg flex items-center animate-expand-top-bottom text-lg w-[350px] h-fit")
        if (icon!=null) add(icon,"w-5 h-5")
        add(Span(title),"ml-2")
    }
}

class ToastLayout:Div() {
    init {
        id = "toast-layout"
        cn("w-full h-screen z-50 flex flex-col items-center max-h-[400px] pt-5 absolute pointer-events-none overflow-y-auto")
    }
}

fun createToast(title:String,icon:Component?=null,duration:Int=5){
    val itemId = "item_${UUID.randomUUID()}"
    val toastItem = ToastItem(title,icon).apply { id=itemId }.render()
    Lumina.executeJS("""
        var toastLayout = document.getElementById("toast-layout");
        toastLayout.innerHTML += `${toastItem}`;
        var toastItem = document.getElementById("$itemId");
        setTimeout(function(){
            toastItem.remove();
        },$duration*1000);
    """.trimIndent())
}