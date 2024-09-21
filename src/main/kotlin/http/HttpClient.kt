package gecw.cse.http

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.bson.BsonDocument
import java.io.File
import java.io.IOException

class HttpClient {

    companion object {
        const val BASE_URL = "http://localhost:3200/api"
    }

    private val client = OkHttpClient()

    // GET Request
    fun get(path: String,onResponse: (data:BsonDocument)->Unit) {
        val url = "$BASE_URL$path"
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            response.body?.string()?.let { onResponse(BsonDocument.parse(it)) }
        }
    }

    // POST Request
    fun post(path: String, json: BsonDocument,onResponse: (data: BsonDocument) -> Unit) {
        val url = "$BASE_URL$path"
        val body = json.toJson().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder().url(url).post(body).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            response.body?.string()?.let { onResponse(BsonDocument.parse(it)) }
        }
    }

    // PUT Request
    fun put(path: String, json: BsonDocument): BsonDocument? {
        val url = "$BASE_URL$path"
        val body = json.toJson().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder().url(url).put(body).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            return response.body?.string()?.let { BsonDocument.parse(it) }
        }
    }

    // DELETE Request
    fun delete(path: String): BsonDocument? {
        val url = "$BASE_URL$path"
        val request = Request.Builder().url(url).delete().build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            return response.body?.string()?.let { BsonDocument.parse(it) }
        }
    }

    //Multipart upload
    fun upload(path: String, file:File,params:HashMap<String,String>,onUploaded:(response:BsonDocument)->Unit){
        val url = "$BASE_URL$path"
        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
        for ((key, value) in params) {
            requestBody.addFormDataPart(key, value)
        }
        requestBody.addFormDataPart("file", file.name, file.asRequestBody("image/*".toMediaType()))
        val request = Request.Builder().url(url).post(requestBody.build()).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { onUploaded(BsonDocument.parse(it)) }
            }
        })
    }

}
