package main.data_classes

import com.squareup.moshi.JsonClass
import main.classes.sealed_classes.ClientMessageType
import java.sql.Timestamp

@JsonClass(generateAdapter = true)
data class ClientOutgoingMessage(
    val type: ClientMessageType,
    val id: String,
    val topic: String,
    val timestamp: Timestamp,
    val payload: Map<String, Any>
)
