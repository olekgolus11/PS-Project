package main.classes.builders

import main.classes.sealed_classes.ClientIncomingMessageMode
import main.classes.sealed_classes.ClientMessageType
import main.data_classes.ClientIncomingMessage
import main.data_classes.ClientOutgoingMessage
import java.sql.Timestamp

class ClientIncomingMessageBuilder {
    private lateinit var id: String
    private lateinit var type: ClientMessageType
    private lateinit var timestamp: Timestamp
    private var topic: String? = null
    private var mode: ClientIncomingMessageMode? = null
    private var payload: Map<String, Any>? = null

    fun setId(id: String) = apply { this.id = id }
    fun setType(type: ClientMessageType) = apply { this.type = type }
    fun setTimestamp(timestamp: Timestamp) = apply { this.timestamp = timestamp }
    fun setTopic(topic: String?) = apply { this.topic = topic }
    fun setMode(mode: ClientIncomingMessageMode?) = apply { this.mode = mode }
    fun setPayload(payload: Map<String, Any>?) = apply { this.payload = payload }

    fun build() = ClientIncomingMessage(id, type, timestamp, topic, mode, payload)
}