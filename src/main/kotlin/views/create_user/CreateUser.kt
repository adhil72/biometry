package gecw.cse.views.create_user

import gecw.ace.lumina.Lumina
import gecw.ace.lumina.ui.common.Button
import gecw.ace.lumina.ui.common.Div
import gecw.ace.lumina.ui.common.Span
import gecw.cse.http.services.createUserService
import gecw.cse.http.services.generateIdService
import gecw.cse.http.services.uploadFinger
import gecw.cse.http.services.verifyFinger
import gecw.cse.lumina.ui.common.*
import gecw.cse.utils.FingerPrintScanner
import gecw.cse.views.common.Layout
import javafx.application.Platform
import java.io.File

val rollNoInput = Input().apply {
    onInput { checkEnableBtnCreateUser() }
    addClass("w-full px-3 py-2 border rounded")
    placeholder = "Enter roll number"
}

val batchInput = Input().apply {
    onInput { checkEnableBtnCreateUser() }
    addClass("w-full px-3 py-2 border rounded uppercase")
    placeholder = "Enter batch"
}

val regNumberInput = Input().apply {
    onInput { checkEnableBtnCreateUser() }
    addClass("w-full px-3 py-2 border rounded")
    placeholder = "Enter roll number"
}

val nameInput = Input().apply {
    onInput {
        checkEnableBtnCreateUser()
    }
    addClass("w-full")
    placeholder = "Enter name"
}

val scanFingerProgress = Progress().apply {
    progressPercentage = 50.0
}

var uid: String = ""
var verified = false

val scanFingerDialog = Dialog(Div().apply {
    cn("w-[500px] flex flex-col items-center bg-white rounded-xl shadow-md p-6")
    add(Span("Scan your finger"), "text-xl font-bold text-black")
    add(Svg.parse("finger"), "w-20 h-20 mt-5 animate-pulse")
    add(scanFingerProgress, "mt-5")
})

val buttonScanFinger = Button(enabled = false, innerComponent = Div().apply {
    cn("flex items-center ")
    add(Svg.parse("finger"), "w-5 h-5")
    add(Span("Scan Fingerprint"), "ml-2")
}).apply {
    onClick {
        createToast("Fingerprint scanned successfully", 3)
    }
    addClass("w-full py-2 bg-black text-white rounded mb-4 flex items-center justify-center")
}

val buttonCreateUser = Button(Div().apply {
    cn("flex items-center ")
    add(Svg.parse("user_add"), "w-5 h-5")
    add(Span("Create user"), "ml-2")
}, false).apply {
    addClass("w-full py-2 bg-black text-white rounded mb-4 flex items-center justify-center")
}

val buttonVerifyUser = Button(Div().apply {
    cn("flex items-center ")
    add(Svg.parse("finger"), "w-5 h-5")
    add(Span("Verify user"), "ml-2")
}, false).apply {
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
            add(nameInput)
        })

        add(Div().apply {
            cn("mb-4")
            add(Span("Batch").apply { cn("block text-gray-700 text-sm font-bold mb-2") })
            add(batchInput)
        })

        add(Div().apply {
            cn("mb-4")
            add(Span("Registration Number").apply { cn("block text-gray-700 text-sm font-bold mb-2") })
            add(regNumberInput)
        })

        add(Div().apply {
            cn("mb-4")
            add(Span("Roll no").apply { cn("block text-gray-700 text-sm font-bold mb-2") })
            add(rollNoInput)
        })

        add(buttonScanFinger)
        add(buttonVerifyUser)
        add(buttonCreateUser)
    })
}

fun checkEnableBtnCreateUser() {
    buttonCreateUser.enabled =
        nameInput.value.isNotEmpty() && uid.isNotEmpty() && batchInput.value.isNotEmpty() && rollNoInput.value.isNotEmpty() && regNumberInput.value.isNotEmpty() && verified
}

var count = 0
fun scanFinger(onDone: () -> Unit) {
    if (count < 5) {
        Thread {
            FingerPrintScanner().scan(File(""), "test")
            Platform.runLater {
                scanFingerProgress.progressPercentage = (count + 1) * 20.0
            }
            count++
            uploadFinger(File("test.bmp"), uid, count) {
                scanFinger(onDone)
            }
        }.start()
    } else {
        count = 0
        onDone()
    }
}

fun generateUid() {
    generateIdService {
        uid = it
        println(it)
        Platform.runLater {
            buttonScanFinger.enabled = true
        }
    }
}

class CreateUserView : Layout(createUserComponent, "create") {
    override fun onCreated() {
        super.onCreated()
        generateUid()

        buttonScanFinger.onClick {
            println("clicking")
            Platform.runLater {
                scanFingerDialog.show = true
                scanFingerProgress.progressPercentage = 0.0
            }

            scanFinger {
                println("done")
                Platform.runLater {
                    scanFingerDialog.show = false
                    buttonScanFinger.enabled = false
                    buttonVerifyUser.enabled = true
                }
            }
        }

        buttonVerifyUser.onClick {
            scanFingerDialog.show = true
            scanFingerProgress.progressPercentage = 0.0
            Thread {
                FingerPrintScanner().scan(File(""), "test")
                verifyFinger(File("test.bmp"), uid) {
                    val data = it
                    Platform.runLater {
                        scanFingerProgress.progressPercentage = 100.0

                        if (data["match"]!!.asBoolean().value) {
                            verified = true
                            buttonVerifyUser.enabled = false
                            checkEnableBtnCreateUser()
                            createToast("User verified successfully", 3, ToastType.SUCCESS)
                        } else {
                            createToast("User verification failed", 3, ToastType.ERROR)
                        }
                        Lumina.wait(1) {
                            scanFingerDialog.show = false
                        }
                    }
                }
            }.start()
        }

        buttonCreateUser.onClick {
            println("create "+ batchInput.value)
            createUserService(nameInput.value, uid, rollNoInput.value, batchInput.value, regNumberInput.value) {
                Platform.runLater {
                    createToast("User created successfully", 1, ToastType.SUCCESS)
                    nameInput.value = ""
                    rollNoInput.value = ""
                    batchInput.value = ""
                    verified = false
                    buttonVerifyUser.enabled = false
                    buttonCreateUser.enabled = false
                    buttonScanFinger.enabled = false
                    uid = ""
                    checkEnableBtnCreateUser()
                    generateUid()
                }
            }
        }

    }
}