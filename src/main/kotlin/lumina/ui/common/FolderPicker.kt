package gecw.cse.lumina.ui.common

import gecw.ace.lumina.ui.common.Button
import gecw.ace.lumina.ui.common.Div
import gecw.ace.lumina.ui.common.Span
import java.io.File

class FolderPicker : Div() {

    var currentPath = "/home"

    // Input for manual folder path entry
    val pathInput = Div().apply {
        cn("w-full p-2 mb-4 bg-gray-100 border rounded-md")
        add(Input(currentPath).apply {
            cn("w-full")
            onBlur {
                try {
                    val f= File(value)
                    if (f.exists()) {
                        currentPath = f.absolutePath
                        setFolders(getFoldersInPath(currentPath)?: mutableListOf())
                    }
                }catch (_:Exception){}
            }
        })
    }

    // Input for search
    val searchInput = Div().apply {
        cn("w-full p-2 mb-4 bg-gray-100 border rounded-md")
        add(Input("").apply {
            cn("w-full")
            setAttribute("placeholder", "Search folders...")
            onInput {
                searchFolders(value)
            }
        })
    }

    val listDiv = Div().apply {
        cn("flex flex-col gap-2 border rounded-xl h-[300px] overflow-y-auto")
    }

    val container = Div().apply {
        cn("w-[400px] p-5 bg-white shadow-xl flex flex-col gap-2 border rounded-xl")
        add(Div().apply {
            cn("flex justify-end gap-2")
            // Select button
            add(Div().apply {
                cn("w-full p-2 bg-green-700 text-center font-semibold text-white rounded-md cursor-pointer hover:bg-green-600 active:bg-green-700 transition-all duration-200 ease-in-out")
                add(Span("Select"), "text-white")
                onClick {
                    onFolderSelect?.invoke(currentPath)
                    open = false
                }
            })
            // Cancel button
            add(Div().apply {
                cn("w-full p-2 bg-red-700 text-center font-semibold text-white rounded-md cursor-pointer hover:bg-red-600 active:bg-red-700 transition-all duration-200 ease-in-out")
                add(Span("Cancel"), "text-white")
                onClick {
                    onFolderSelect?.invoke("NA")
                    open = false
                }
            })
        })
        add(pathInput)
        add(searchInput)
        add(listDiv)
    }

    var open = false
        set(value) {
            field = value
            if (value) {
                addClass("flex")
                removeClass("hidden")
            } else {
                addClass("hidden")
                removeClass("flex")
            }
        }

    var onFolderSelect: ((String) -> Unit)? = null

    // Method to get folders in a given path
    fun getFoldersInPath(path: String): List<File>? {
        val files = File(path).listFiles()
        return files?.filter { it.isDirectory && !it.name.startsWith(".") }
    }

    // Method to update the UI with a list of folders
    fun setFolders(folders: List<File>) {
        listDiv.apply {
            removeChilds() // Clear previous folders
            // Back button
            add(Div().apply {
                cn("flex items-center text-blue-700 gap-2 p-3 hover:bg-gray-100 active:bg-gray-200 rounded-md cursor-pointer transition-all duration-200 ease-in-out")
                add(Svg.parse("back"), "w-5 h-5")
                add("Back")
                onClick {
                    val parent = File(currentPath).parentFile
                    if (parent != null) {
                        val subFolders = getFoldersInPath(parent.absolutePath)
                        currentPath = parent.absolutePath
                        if (subFolders != null) {
                            setFolders(subFolders)
                        }
                    }
                }
            })
            // Display folders
            folders.forEach {
                add(Div().apply {
                    cn("flex items-center gap-2 p-3 hover:bg-gray-100 active:bg-gray-200 rounded-md cursor-pointer transition-all duration-200 ease-in-out")
                    add(Svg.parse("folder"), "w-5 h-5")
                    add(Span(it.name), "text-black")
                    onClick {
                        val subFolders = getFoldersInPath(it.absolutePath)
                        currentPath = it.absolutePath
                        if (subFolders != null) {
                            setFolders(subFolders)
                        }
                    }
                })
            }
        }
    }

    // Method to search folders based on user input
    fun searchFolders(query: String) {
        val folders = getFoldersInPath(currentPath)?.filter {
            it.name.contains(query, ignoreCase = true)
        }
        if (folders != null) {
            setFolders(folders)
        }
    }

    // Initialize the FolderPicker
    init {
        cn("w-full hidden flex items-center fixed inset-0 z-50 justify-center bg-white bg-opacity-60 rounded-md p-5")
        add(container)

        val folders = getFoldersInPath(currentPath)
        if (folders != null) {
            setFolders(folders)
        }

        pathInput.children[0].attributes["value"] = currentPath
    }

    // Method to allow folder selection
    fun pickFolder(onSelect: (String) -> Unit) {
        onFolderSelect = onSelect
        open = true
    }
}
