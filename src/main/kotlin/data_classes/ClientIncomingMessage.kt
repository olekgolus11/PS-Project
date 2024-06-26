package main.data_classes

import com.squareup.moshi.JsonClass
import main.classes.sealed_classes.ClientIncomingMessageMode
import classes.sealed_classes.ClientMessageType
import java.sql.Timestamp

@JsonClass(generateAdapter = true)
data class ClientIncomingMessage(
    val id: String,
    val type: ClientMessageType,
    val timestamp: Timestamp,
    val topic: String? = null,
    val mode: ClientIncomingMessageMode? = null,
    val payload: Map<String, Any>? = null
) {
    override fun toString(): String {
        return """
            ClientIncomingMessage {
                id = '$id',
                type = $type,
                timestamp = $timestamp,
                topic = $topic,
                mode = $mode,
                payload = $payload
            }
        """.trimIndent()
    }
}
