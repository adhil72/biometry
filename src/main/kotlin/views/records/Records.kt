package gecw.cse.views.records

import gecw.ace.lumina.Lumina
import gecw.ace.lumina.ui.common.Button
import gecw.ace.lumina.ui.common.Div
import gecw.ace.lumina.ui.common.Span
import gecw.cse.http.HttpClient
import gecw.cse.http.services.getRecordsService
import gecw.cse.lumina.ui.common.*
import gecw.cse.utils.DownloadManager
import gecw.cse.utils.FileUtils
import gecw.cse.views.common.Layout
import javafx.application.Platform
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

val Table = Table().apply {
    cn("w-full border-collapse border shadow")
    add(Tr().apply {
        add(Th("Name"))
        add(Th("Roll number"))
        add(Th("In"))
        add(Th("Out"))
    })
}

fun clearTable() {
    Table.apply {
        removeChilds()
        add(Tr().apply {
            add(Th("Name"))
            add(Th("Roll number"))
            add(Th("In"))
            add(Th("Out"))
        })
    }
}


val semSelect = Select().apply {
    cn("flex-1")
    add(Option("Select semester", ""))
    for (i in 1..8) add(Option("Semester $i", i.toString()))
}

val dateSelect = Input("date").apply {
    cn("flex-1 p-2 border border-gray-300 rounded-md dpdpdp")
    placeholder = "Select date"
}

val sessionSelect = Select().apply {
    cn("flex-1")
    add(Option("Select session", ""))
    add(Option("Morning", "morning"))
    add(Option("Afternoon", "afternoon"))
    add(Option("Special", "special"))
}

val exportButton = Button(Span("Export")).apply {
    cn("w-full py-2 bg-blue-500 text-white rounded-md mt-5")
    onClick {
        Lumina.executeJS("window.open(`/export?date=${dateSelect.value}&sem=${semSelect.value}&session=${sessionSelect.value}')")
    }
}

val folderPicker = FolderPicker()

val recordsComponent = Div().apply {
    cn("w-full h-screen bg-[#f2f2f2] flex flex-col items-center justify-center")
    add(folderPicker)
    add(Div().apply {
        cn("bg-white w-[80%] p-5 rounded-xl shadow-md")
        add(Div().apply {
            cn("grid grid-cols-3 w-full gap-x-5 my-5")
            add(dateSelect)
            add(semSelect)
            add(sessionSelect)
        })
        add(Table)
        add(Div().apply {
            cn("w-full flex justify-end")
            add(exportButton)
        })
    })
}

fun fetchRecords() {
    Platform.runLater { clearTable() }
    if (dateSelect.value != "" && semSelect.value != "" && sessionSelect.value.isNotEmpty()) getRecordsService(dateSelect.value, semSelect.value, sessionSelect.value) {
        Platform.runLater {
            exportButton.enabled = true
            it.forEach {
                Table.add(Tr().apply {
                    it.asDocument()["name"]?.asString()?.value?.let { it1 -> add(Td(it1)) }
                    it.asDocument()["rollNumber"]?.asString()?.value?.let { it1 -> add(Td(it1)) }
                    val logs = it.asDocument()["logs"]?.asArray()
                    var inTime = "Na"
                    var outTime = "Na"
                    logs?.forEach {
                        if (it.asDocument()["type"]?.asString()?.value == "in") {
                            val timeInSeconds = it.asDocument()["time"]?.asDateTime()?.value
                            val timeString = timeInSeconds?.let {
                                val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
                                dateTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
                            }
                            inTime = timeString ?: "Na"
                        } else {
                            val timeInSeconds = it.asDocument()["time"]?.asDateTime()?.value
                            val timeString = timeInSeconds?.let {
                                val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
                                dateTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
                            }
                            outTime = timeString ?: "Na"
                        }
                    }
                    add(Td(inTime))
                    add(Td(outTime))
                })
            }
        }
    }
}

class Records : Layout(recordsComponent, "records") {
    override fun onCreated() {
        super.onCreated()
        Lumina.executeJS("flatpickr('.dpdpdp', {dateFormat: 'Y-m-d'})")
        dateSelect.onChange { fetchRecords() }
        semSelect.onChange { fetchRecords() }
        sessionSelect.onChange { fetchRecords() }

        exportButton.onClick {
            folderPicker.pickFolder {
                if (it.isNotEmpty()){
                    DownloadManager().download(HttpClient.BASE_URL+"/export/csv?sem=${semSelect.value}&date=${dateSelect.value}&session=${sessionSelect.value}", it,"records",false)
                }
            }
        }
    }
}