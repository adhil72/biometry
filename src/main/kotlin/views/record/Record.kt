package gecw.cse.views.record

import gecw.ace.lumina.Lumina
import gecw.ace.lumina.ui.common.Button
import gecw.ace.lumina.ui.common.Div
import gecw.ace.lumina.ui.common.Span
import gecw.cse.http.services.detectFingerService
import gecw.cse.lumina.ui.common.*
import gecw.cse.utils.FingerPrintScanner
import gecw.cse.views.common.Layout
import javafx.application.Platform
import java.io.File
import kotlin.properties.Delegates

var scanning = false

val semSelect = Select().apply {
    onChange { checkEnableBtnRecord() }
    cn("w-full")
    add(Option("Select semester", ""))
    for (i in 1..8) {
        add(Option("Semester $i", i.toString()))
    }
}

val typeSelect = Select().apply {
    onChange { checkEnableBtnRecord() }
    cn("w-full")
    add(Option("Select type", ""))
    add(Option("In", "in"))
    add(Option("Out", "out"))
}

val sessionSelect = Select().apply {
    onChange { checkEnableBtnRecord() }
    cn("w-full")
    add(Option("Select session", ""))
    add(Option("Morning", "morning"))
    add(Option("Afternoon", "afternoon"))
    add(Option("Special", "special"))
}

val buttonRecord = Button(enabled = false, innerComponent = Div().apply {
    cn("flex items-center ")
    add(Svg.parse("finger"), "w-5 h-5")
    add(Span("Start recording"), "ml-2")
}).apply {
    cn("w-full py-2 flex justify-center")
}

fun checkEnableBtnRecord() {
    buttonRecord.enabled = semSelect.value.isNotEmpty() && typeSelect.value.isNotEmpty() && sessionSelect.value.isNotEmpty()
}

val scanFingerDialog = Dialog(Div().apply {
    cn("w-[500px] flex flex-col items-center bg-white rounded-xl shadow-md p-6")
    add(Span("Scan your finger"), "text-xl font-bold text-black")
    add(Svg.parse("finger"), "w-20 h-20 mt-5 animate-pulse")
}, object : DialogListener {
    override fun onClose() {}
    override fun onManualClose() {
        exit = true
    }
})

val checkInName = Span("Name").apply {
    cn("text-xl font-bold text-black")
}

val checkInRoll = Span("Roll number").apply {
    cn("text-xl font-bold text-black")
}

val checkInSvg = Svg.parse("check")

val checkInDialog = Dialog(Div().apply {
    add(Span("Detected"), "text-xl font-bold text-black")
    cn("w-[500px] flex flex-col items-center bg-white rounded-xl shadow-md p-6")
    add(checkInSvg, "w-20 h-20 text-white fill-green-600")
    add(checkInName)
    add(checkInRoll)

}, object : DialogListener {
    override fun onClose() {}
    override fun onManualClose() {
        exit = true
    }
})

val recordCheckInComponent = Div().apply {
    cn("flex-1 h-screen flex justify-center")
    add(Div().apply {
        addClass("p-6 bg-white rounded-lg shadow-md min-w-[500px] h-fit mt-5")

        add(Span("Record Check-In").apply {
            cn("text-2xl font-bold mb-6")
        })

        add(Div().apply {
            cn("mb-4")
            add(Span("Semester").apply { cn("block text-gray-700 text-sm font-bold mb-2") })
            add(semSelect)
        })

        add(Div().apply {
            cn("mb-4")
            add(Span("Type").apply { cn("block text-gray-700 text-sm font-bold mb-2") })
            add(typeSelect)
        })

        add(Div().apply {
            cn("mb-4")
            add(Span("Type").apply { cn("block text-gray-700 text-sm font-bold mb-2") })
            add(sessionSelect)
        })

        add(buttonRecord)
        add(scanFingerDialog)
        add(checkInDialog)
    })
}

var exit: Boolean by Delegates.observable(false) { _, _, new ->
    println(new)
    println("finger process found: ${FingerPrintScanner.process != null}")
    if (new) {
        FingerPrintScanner.process?.destroy()
    }
}

fun scan() {
    val semValue = semSelect.value
    val typeValue = typeSelect.value
    val sessionValue = sessionSelect.value
    Thread {
        if (exit) return@Thread
        FingerPrintScanner().scan(File(""), "test")
        if (File("test.bmp").exists()) Platform.runLater {
            detectFingerService(File("test.bmp"), semValue, typeValue, sessionValue) {
                Platform.runLater {
                    val matched = it["match"]?.asBoolean()?.value == true
                    if (matched){
                        checkInName.value = it["name"]?.asString()?.value.toString()
                        checkInRoll.value = it["rollNumber"]?.asString()?.value.toString()
                    }else{
                        checkInName.value = "NA"
                        checkInRoll.value = "NA"
                    }
                    checkInDialog.show = true
                    scanning = false
                    scanFingerDialog.show = false
                    Lumina.wait(3) {
                        checkInDialog.show = false
                        if (!exit) {
                            scanFingerDialog.show = true
                            scanning = true
                            scan()
                        } else {
                            FingerPrintScanner.process?.destroy()
                        }
                    }
                }
            }
        }
    }.start()
}

class RecordView : Layout(recordCheckInComponent, "record") {
    init {
        checkEnableBtnRecord()
        buttonRecord.onClick {
            scanFingerDialog.show = true
            exit = false
            scanning = true
            scan()
        }
    }
}
