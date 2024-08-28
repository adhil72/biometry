package gecw.ace.lumina.utils

import java.util.UUID

fun generateRandomUid(): String {
    return UUID.randomUUID().toString()
}