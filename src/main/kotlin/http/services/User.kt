package gecw.cse.http.services

import gecw.cse.http.HttpClient
import org.bson.BsonDocument
import org.bson.BsonString

fun createUserService(
    name: String,
    uid: String,
    rollNumber: String,
    semester: String,
    regNo: String,
    onResponse: (data: BsonDocument) -> Unit
) {
    HttpClient().post("/create/user", BsonDocument().apply {
        append("uid", BsonString(uid))
        append("name", BsonString(name))
        append("rollNumber", BsonString(rollNumber))
        append("batch", BsonString(semester))
        append("regNo", BsonString(regNo))
    }, onResponse)
}