package gecw.cse.utils

import java.io.File

class FileUtils {
    companion object{
        fun moveFileToFolder(file:File,toFolder:File){
            file.renameTo(File(toFolder,file.name))
        }

        fun moveFolderToFolder(folder:File,toFolder: File){
            folder.renameTo(File(toFolder,folder.name))
        }
    }
}