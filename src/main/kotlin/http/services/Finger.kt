package gecw.cse.http.services

import gecw.cse.http.HttpClient
import gecw.cse.utils.FingerPrintScanner
import org.bson.BsonDocument
import java.io.File

fun uploadFinger(file: File, uid: String, count: Int, onResponse: (response: BsonDocument) -> Unit) {
    HttpClient().upload("/finger/create", file, hashMapOf("uid" to uid, "count" to count.toString())) {
        onResponse(it)
    }
}

fun verifyFinger(file: File, uid: String, onResponse: (response: BsonDocument) -> Unit) {
    HttpClient().upload("/finger/verify", file, hashMapOf("uid" to uid)) {
        onResponse(it)
    }
}

fun detectFingerService(file: File, batch: String, type: String, session:String, onResponse: (response: BsonDocument) -> Unit) {
    HttpClient().upload("/finger/detect", file, hashMapOf("batch" to batch, "type" to type, "session" to session)) {
        File("test.bmp").delete()
        onResponse(it)
    }
}

fun destroyScanService() {
    Thread {
        FingerPrintScanner.process?.destroy()
    }.start()
}