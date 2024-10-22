package gecw.cse.http.services

import gecw.cse.http.HttpClient
import org.bson.BsonArray

fun generateIdService(onGenerated: (uid: String) -> Unit) {
    HttpClient().get("/generate/uid") {
        onGenerated(it.getString("uid").value)
    }
}

fun getRecordsService(date: String, batch: String, session:String, onRecords: (records: BsonArray) -> Unit) {
    HttpClient().get("/records?date=$date&batch=$batch&session=$session") {
        onRecords(it.getArray("data"))
    }
}

fun testConnectionService(onSuccess: () -> Unit, onFailure: () -> Unit) {
    try {
        println("testing")
        HttpClient().get("/") {
            onSuccess();
            println("testing")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        onFailure()
    }
}