package main.classes.builders
import classes.sealed_classes.ClientMessageType
import main.data_classes.ClientIncomingMessage
import main.data_classes.ClientOutgoingMessage
import java.sql.Timestamp

class ClientOutgoingMessageBuilder {
    private lateinit var id: String
    private lateinit var type: ClientMessageType
    private lateinit var timestamp: Timestamp
    private var topic: String? = null
    private var payload: Map<String, Any>? = null

    fun setId(id: String) = apply { this.id = id }
    fun setType(type: ClientMessageType) = apply { this.type = type }
    fun setTimestamp(timestamp: Timestamp) = apply { this.timestamp = timestamp }
    fun setTopic(topic: String?) = apply { this.topic = topic }
    fun setPayload(payload: Map<String, Any>?) = apply { this.payload = payload }

    fun copy(clientIncomingMessage: ClientIncomingMessage) = apply {
        this.id = clientIncomingMessage.id
        this.type = clientIncomingMessage.type
        this.timestamp = clientIncomingMessage.timestamp
        this.topic = clientIncomingMessage.topic
        this.payload = clientIncomingMessage.payload
    }

    fun copy(clientOutgoingMessage: ClientOutgoingMessage) = apply {
        this.id = clientOutgoingMessage.id
        this.type = clientOutgoingMessage.type
        this.timestamp = clientOutgoingMessage.timestamp
        this.topic = clientOutgoingMessage.topic
        this.payload = clientOutgoingMessage.payload
    }

    fun build() = ClientOutgoingMessage(id, type, timestamp, topic, payload)
}