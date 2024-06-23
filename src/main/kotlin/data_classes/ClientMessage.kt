package main.data_classes

import com.squareup.moshi.JsonClass
import main.classes.sealed_classes.ClientMessageMode
import main.classes.sealed_classes.ClientMessageType
import java.sql.Timestamp

@JsonClass(generateAdapter = true)
data class ClientMessage(
    val type: ClientMessageType,
    val id: String,
    val topic: String,
    val mode: ClientMessageMode,
    val timestamp: Timestamp,
    val payload: Map<String, Any>
)
