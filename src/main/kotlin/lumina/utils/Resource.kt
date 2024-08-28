package gecw.ace.lumina.utils

import java.net.URL

class Resource {
    companion object {
        fun get(name: String): URL {
            val i = Resource::class.java.getResource("/$name") ?: throw Exception("Resource not found")
            return i
        }

        fun getAsString(name: String): String {
            return get(name).readText()
        }

        fun getAsBytes(name: String): ByteArray {
            return get(name).readBytes()
        }

        fun getAsStream(name: String): ByteArray {
            return get(name).openStream().readBytes()
        }

        fun icon(s: String): String {
            return get("icons/$s").toExternalForm()
        }
    }
}
