package gecw.cse.utils

import com.machinezoo.sourceafis.FingerprintImage
import com.machinezoo.sourceafis.FingerprintImageOptions
import com.machinezoo.sourceafis.FingerprintMatcher
import com.machinezoo.sourceafis.FingerprintTemplate
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*


class FingerPrintScanner {

    companion object {
        var process: Process? = null
    }

    fun scan(folder: File, fileName: String): String? {
        return exec(File("driver").absolutePath, folder, fileName)
    }

    private fun exec(cmd: String, folder: File, fileName: String): String? {
        File("prints").mkdir()
        if (process != null) {
            process!!.destroy()
        }
        try {
            java.awt.Toolkit.getDefaultToolkit().beep()

            process = Runtime.getRuntime().exec(cmd)
            process?.waitFor()

            var imgFile = File("frame_Ex.bmp")
            val fileNameExt = "$fileName.bmp"
            renameFile(imgFile, fileNameExt)
            imgFile = File(fileNameExt)
            moveFileToFolder(imgFile, folder)
            return fileNameExt
        } catch (_: Exception) { }
        return null
    }

    private fun getCurrentTimeString(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
        val date: Date = Date()
        return formatter.format(date)
    }

    private fun renameFile(file: File, toName: String) {
        if (file.exists()) {
            val newFile = File(file.parent, toName)
            file.renameTo(newFile)
        }
    }

    private fun moveFileToFolder(file: File, folder: File) {
        if (file.exists() && folder.exists()) {
            val newFile = File(folder, file.name)
            file.renameTo(newFile)
        }
    }

    @Throws(IOException::class)
    fun detectFingerprint(sem:String): String {
        val folder = File("fingerprints", sem)
        scan(File(""), "test")
        val fingerprintFiles = folder.list() ?: return "not"
        for (i in 0..4) {
            val result = detectFingerprintInFolder(folder,fingerprintFiles,i)
            if (result != "not") {
                return result
            }
        }
        return "not"
    }
    
    private fun detectFingerprintInFolder(folder:File,fingerprintFiles: Array<String>, i:Int): String {
        for (fingerprints in fingerprintFiles){
            val fingerprintFile = File(folder.absolutePath+"/"+fingerprints, "$i.bmp")

            val probe = FingerprintTemplate(
                FingerprintImage(
                    Files.readAllBytes(Paths.get(File("test.bmp").path)), FingerprintImageOptions().dpi(500.0)
                )
            )

            val candidate = FingerprintTemplate(
                FingerprintImage(
                    Files.readAllBytes(Paths.get(fingerprintFile.path)), FingerprintImageOptions().dpi(500.0)
                )
            )

            val score: Double = FingerprintMatcher(probe).match(candidate)

            val matches = score >= 40
            if (matches) return fingerprints
        }
        return "not"
    }

    fun validateFingerprint(uid: String): Boolean {
        scan(File(""), "test")
        val folder = File("prints", uid)
        val fingerprintFiles = folder.listFiles { dir: File?, name: String ->
            name.endsWith(
                ".bmp"
            )
        }

        for (i in fingerprintFiles.indices) {
            val probe = FingerprintTemplate(
                FingerprintImage(
                    Files.readAllBytes(Paths.get(File("test.bmp").path)), FingerprintImageOptions().dpi(500.0)
                )
            )
            val candidate = FingerprintTemplate(
                FingerprintImage(
                    Files.readAllBytes(
                        Paths.get(
                            fingerprintFiles[i].path
                        )
                    ), FingerprintImageOptions().dpi(500.0)
                )
            )
            val score: Double = FingerprintMatcher(probe).match(candidate)
            val matches = score >= 40
            if (matches) {
                return true
            }
        }
        return false
    }
}