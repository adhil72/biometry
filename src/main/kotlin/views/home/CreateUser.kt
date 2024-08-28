package gecw.cse.views.home

import gecw.ace.lumina.ui.common.Button
import gecw.ace.lumina.ui.common.Div
import gecw.ace.lumina.ui.common.Span
import gecw.cse.lumina.ui.common.*
import gecw.cse.views.common.Layout

var count = 0

val input = Input().apply {
    addClass("w-full")
    placeholder = "Enter name"
}

val scanFingerProgress = Progress().apply {
    progressPercentage = 50.0
}

val scanFingerDialog = Dialog(Div().apply {
    cn("w-[500px] flex flex-col items-center bg-white rounded-xl shadow-md p-6")
    add(Span("Scan your finger"), "text-xl font-bold text-black")
    add(Svg.parse("finger"), "w-20 h-20")
    add(scanFingerProgress)
})

val buttonScanFinger = Button(Div().apply {
    cn("flex items-center ")
    add(Svg.parse("finger"), "w-5 h-5")
    add(Span("Scan Fingerprint"), "ml-2")
}).apply {
    enabled = false
    onClick {
        createToast("Fingerprint scanned successfully", Svg.parse("finger"), 5)
    }
    addClass("w-full py-2 bg-black text-white rounded mb-4 flex items-center justify-center")
}

val buttonCreateUser = Button(Div().apply {
    cn("flex items-center ")
    add(Svg.parse("user_add"), "w-5 h-5")
    add(Span("Create user"), "ml-2")
}).apply {
    enabled = false
    addClass("w-full py-2 bg-black text-white rounded mb-4 flex items-center justify-center")
}

val createUserComponent = Div().apply {
    cn("flex-1 h-screen flex justify-center")
    add(scanFingerDialog)
    add(Div().apply {
        addClass("p-6 bg-white rounded-lg shadow-md min-w-[500px] h-fit mt-5")

        add(Span("Create New User").apply {
            cn("text-2xl font-bold mb-6")
        })

        add(Div().apply {
            cn("mb-4")
            add(Span("Name").apply { cn("block text-gray-700 text-sm font-bold mb-2") })
            add(input)
        })

        add(Div().apply {
            cn("mb-4")
            add(Span("Semester").apply { cn("block text-gray-700 text-sm font-bold mb-2") })
            add(Select().apply {
                cn("w-full")
                add(Option("Select semester", ""))
                for (i in 1..8) {
                    add(Option("Semester $i", i.toString()))
                }
            })
        })

        add(Div().apply {
            cn("mb-4")
            add(Span("Roll Number").apply { cn("block text-gray-700 text-sm font-bold mb-2") })
            add(Input().apply {
                addClass("w-full px-3 py-2 border rounded")
                placeholder = "Enter roll number"
            })
        })

        add(buttonScanFinger)
        add(buttonCreateUser)
    })
}

class CreateUserView : Layout(createUserComponent, "create")