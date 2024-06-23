package main.classes.builders
import main.classes.sealed_classes.ClientIncomingMessageMode
import main.classes.sealed_classes.ClientMessageType
import main.data_classes.ClientIncomingMessage
import main.data_classes.ClientOutgoingMessage
import java.sql.Timestamp

class ClientIncomingMessageBuilder {
    private lateinit var type: ClientMessageType
    private lateinit var id: String
    private lateinit var topic: String
    private lateinit var mode: ClientIncomingMessageMode
    private lateinit var timestamp: Timestamp
    private lateinit var payload: Map<String, Any>

    fun setType(type: ClientMessageType) = apply { this.type = type }
    fun setId(id: String) = apply { this.id = id }
    fun setTopic(topic: String) = apply { this.topic = topic }
    fun setMode(mode: ClientIncomingMessageMode) = apply { this.mode = mode }
    fun setTimestamp(timestamp: Timestamp) = apply { this.timestamp = timestamp }
    fun setPayload(payload: Map<String, Any>) = apply { this.payload = payload }

    fun build() = ClientIncomingMessage(type, id, topic, mode, timestamp, payload)
}