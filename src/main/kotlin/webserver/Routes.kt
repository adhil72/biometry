package gecw.ace.webserver

import gecw.ace.db.MongoDbManager
import gecw.cse.utils.DateUtils
import gecw.cse.utils.FileUtils
import gecw.cse.utils.FingerPrintScanner
import gecw.cse.utils.Uid
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.bson.BsonBoolean
import org.bson.BsonDocument
import org.bson.BsonString
import org.bson.Document
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.util.*

@RestController
class Routes {
    @GetMapping("/generate/uid")
    fun generateUid(): String {
        val uid = Uid.generate()
        File("prints", uid).mkdir()
        return BsonDocument().apply {
            append("uid", BsonString(uid))
        }.toString()
    }

    @PostMapping("/scan/finger")
    fun scanFinger(@RequestBody body: String): String {
        val data = BsonDocument.parse(body)
        val uid = data.getString("uid").value
        val count = data.getString("count").value

        FingerPrintScanner().scan(File("prints", uid), count)

        return BsonDocument().apply {
            append("uid", BsonString(uid))
        }.toString()
    }

    @PostMapping("/scan/verify")
    fun verifyScan(@RequestBody body: String): String {
        val data = BsonDocument.parse(body)
        val uid = data.getString("uid").value
        val valid = FingerPrintScanner().validateFingerprint(uid)

        println(valid)

        return BsonDocument().apply {
            append("valid", BsonBoolean(valid))
        }.toString()
    }

    @PostMapping("/create/user")
    fun createEntry(@RequestBody body: String): String {
        println(body)
        val data = BsonDocument.parse(body)
        val uid = data.getString("uid").value
        val name = data.getString("name").value
        val rollNumber = data.getString("rollNumber").value
        val semester = data.getString("semester").value
        File("fingerprints", semester).mkdirs()

        FileUtils.moveFolderToFolder(File("prints", uid), File("fingerprints/$semester"))

        MongoDbManager.mongoClient.getDatabase("ace").getCollection("users").insertOne(Document().apply {
            append("_id", uid)
            append("name", name)
            append("rollNumber", rollNumber)
            append("semester", semester)
            append("created_at", Date())
            append("updated_at", Date())
        })

        return BsonDocument().apply {
            append("status", BsonString("success"))
        }.toString()
    }

    @GetMapping("/records")
    fun getAllRecords(@RequestParam date: String, @RequestParam sem: String): String {
        val users = MongoDbManager.mongoClient.getDatabase("ace").getCollection("users").find(Document().apply {
            append("semester", sem + "")
        }).toList()

        if (users.isEmpty()) return "[]"

        var jsonData = "["
        for (user in users) {
            val logs = MongoDbManager.mongoClient.getDatabase("ace").getCollection("logs").find(Document().apply {
                append("uid", user.getString("_id"))
                append("time", Document().apply {
                    append("\$gte", DateUtils.isoStringToDate("${date}T00:00:00.000Z"))
                    append("\$lt", DateUtils.isoStringToDate("${date}T23:59:59.999Z"))
                })
            }).toList()
            user.append("logs", logs)
            jsonData += user.toJson() + ","
        }
        jsonData = jsonData.substring(0, jsonData.length - 1) + "]"
        return jsonData
    }

    @GetMapping("/record/kill")
    fun killRecording(): String {
        try {
            FingerPrintScanner.process?.destroy()
        } catch (_: Exception) {
        }
        return BsonDocument().toJson().toString()
    }

    @PutMapping("/record")
    fun record(@RequestParam sem: String, type: String): String {
        val uid = FingerPrintScanner().detectFingerprint(sem)
        val user = MongoDbManager.mongoClient.getDatabase("ace").getCollection("users").find(Document().apply {
            append("_id", uid)
        }).firstOrNull()

        return if (user == null) {
            BsonDocument().apply {
                append("match", BsonBoolean(false))
            }.toString()
        } else {

            val todayLog = MongoDbManager.mongoClient.getDatabase("ace").getCollection("logs").find(Document().apply {
                append("uid", uid)
                append("type", type)
                append("time", Document().apply {
                    append("\$gte", Date().apply {
                        hours = 0
                        minutes = 0
                        seconds = 0
                    })
                })
            }).first()

            println(todayLog)

            if (todayLog != null) return user.apply {
                append("match", BsonBoolean(true))
            }.toJson().toString()

            MongoDbManager.mongoClient.getDatabase("ace").getCollection("logs").insertOne(Document().apply {
                append("uid", uid)
                append("time", Date())
                append("type", type)
            })
            user.apply {
                append("match", BsonBoolean(true))
            }.toJson().toString()
        }
    }

    @GetMapping("/export/json")
    fun exportStudentsAsJson(@RequestParam sem: String): ResponseEntity<ByteArrayResource> {
        val users = MongoDbManager.mongoClient.getDatabase("ace").getCollection("users").find(Document().apply {
            append("semester", sem)
        }).toList()

        val json = if (users.isEmpty()) "[]" else users.joinToString(
            separator = ",",
            prefix = "[",
            postfix = "]"
        ) { it.toJson() }

        val f = File("temps","students_${Uid.generate()}_$sem.json")
        f.writeText(json)

        val resource = ByteArrayResource(f.readBytes())

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students_${Uid.generate()}_$sem.json")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .contentLength(f.length())
            .body(resource)
    }

    @GetMapping("/export/csv")
    fun exportStudentsAsCsv(@RequestParam sem: String): ResponseEntity<ByteArrayResource> {
        val users = MongoDbManager.mongoClient.getDatabase("ace").getCollection("users").find(Document().apply {
            append("semester", sem)
        }).toList()

        val outStream = ByteArrayOutputStream()
        val writer = OutputStreamWriter(outStream, StandardCharsets.UTF_8)
        val csvPrinter = CSVPrinter(
            writer,
            CSVFormat.DEFAULT.withHeader("UID", "Name", "Roll Number", "Semester", "Created At", "Updated At")
        )

        users.forEach { user ->
            csvPrinter.printRecord(
                user.getString("_id"),
                user.getString("name"),
                user.getString("rollNumber"),
                user.getString("semester"),
                user.getDate("createdAt")?.toInstant()?.toString() ?: "",
                user.getDate("updatedAt")?.toInstant()?.toString() ?: ""
            )
        }
        csvPrinter.flush()
        writer.close()

        // Create a ByteArrayResource for the file
        val byteArray = outStream.toByteArray()
        val resource = ByteArrayResource(byteArray)

        // Return ResponseEntity with content type and attachment header
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students_${Uid.generate()}_$sem.csv")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .contentLength(byteArray.size.toLong())
            .body(resource)
    }

    @GetMapping("/dashboard")
    fun dashboard(): String {
        val studentsCount = MongoDbManager.mongoClient.getDatabase("ace").getCollection("users").countDocuments()
        val todayInLogs =
            MongoDbManager.mongoClient.getDatabase("ace").getCollection("logs").countDocuments(Document().apply {
                append("time", Document().apply {
                    append("\$gte", Date().apply {
                        hours = 0
                        minutes = 0
                        seconds = 0
                    })
                })
                append("type", "in")
            })
        val todayOutLogs =
            MongoDbManager.mongoClient.getDatabase("ace").getCollection("logs").countDocuments(Document().apply {
                append("time", Document().apply {
                    append("\$gte", Date().apply {
                        hours = 0
                        minutes = 0
                        seconds = 0
                    })
                })
                append("type", "out")
            })

        val totalRecords = MongoDbManager.mongoClient.getDatabase("ace").getCollection("logs").countDocuments()

        return BsonDocument().apply {
            append("students", BsonString(studentsCount.toString()))
            append("todayIn", BsonString(todayInLogs.toString()))
            append("todayOut", BsonString(todayOutLogs.toString()))
            append("totalRecords", BsonString(totalRecords.toString()))
        }.toJson().toString()
    }
}