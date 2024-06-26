package main.data_classes

import com.squareup.moshi.JsonClass
import classes.sealed_classes.ClientMessageType
import java.sql.Timestamp

@JsonClass(generateAdapter = true)
data class ClientOutgoingMessage(
    val id: String,
    val type: ClientMessageType,
    val timestamp: Timestamp,
    val topic: String? = null,
    val payload: Map<String, Any>? = null
) {
    override fun toString(): String {
        return """
            ClientOutgoingMessage {
                id = '$id',
                type = $type,
                timestamp = $timestamp,
                topic = $topic,
                payload = $payload
            }
        """.trimIndent()
    }
}
