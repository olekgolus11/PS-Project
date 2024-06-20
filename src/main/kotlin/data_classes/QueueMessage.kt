package main.data_classes

import main.classes.sealed_classes.QueueMessageMode
import main.classes.sealed_classes.QueueMessageType
import java.sql.Timestamp

data class QueueMessage(
    val type: QueueMessageType,
    val id: String,
    val topic: String,
    val mode: QueueMessageMode,
    val timestamp: Timestamp,
    val payload: Map<String, Any>
)
