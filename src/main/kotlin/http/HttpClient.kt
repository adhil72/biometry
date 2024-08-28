package gecw.cse.http

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.bson.BsonDocument
import java.io.IOException

class HttpClient {

    companion object {
        private const val BASE_URL = "http://localhost:3200"
    }

    private val client = OkHttpClient()

    // GET Request
    fun get(path: String): BsonDocument? {
        val url = "$BASE_URL/$path"
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.body?.string()?.let { BsonDocument.parse(it) }
        }
    }

    // POST Request
    fun post(path: String, json: BsonDocument): BsonDocument? {
        val url = "$BASE_URL/$path"
        val body = json.toJson().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder().url(url).post(body).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            return response.body?.string()?.let { BsonDocument.parse(it) }
        }
    }

    // PUT Request
    fun put(path: String, json: BsonDocument): BsonDocument? {
        val url = "$BASE_URL/$path"
        val body = json.toJson().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder().url(url).put(body).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            return response.body?.string()?.let { BsonDocument.parse(it) }
        }
    }

    // DELETE Request
    fun delete(path: String): BsonDocument? {
        val url = "$BASE_URL/$path"
        val request = Request.Builder().url(url).delete().build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            return response.body?.string()?.let { BsonDocument.parse(it) }
        }
    }
}
