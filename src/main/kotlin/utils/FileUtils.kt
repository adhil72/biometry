package gecw.cse.utils

import javafx.stage.DirectoryChooser
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileSystemView

class FileUtils {
    companion object{
        fun moveFileToFolder(file:File,toFolder:File){
            file.renameTo(File(toFolder,file.name))
        }

        fun moveFolderToFolder(folder:File,toFolder: File){
            folder.renameTo(File(toFolder,folder.name))
        }

        fun selectFolder():String{
            val fileChooser = JFileChooser(FileSystemView.getFileSystemView().homeDirectory)
            fileChooser.dialogTitle = "Select Folder"
            fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY

            val returnValue = fileChooser.showDialog(null, "Select")

            return if (returnValue == JFileChooser.APPROVE_OPTION) {
                val selectedFolder: File = fileChooser.selectedFile
                println("Selected Folder: ${selectedFolder.absolutePath}")
                selectedFolder.absolutePath
            } else {
                println("No folder selected.")
                ""
            }
        }
    }
}