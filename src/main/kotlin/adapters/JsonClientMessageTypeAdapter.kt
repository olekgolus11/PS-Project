package main.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import main.classes.sealed_classes.ClientMessageType

class JsonClientMessageTypeAdapter {
    @ToJson
    fun toJson(type: ClientMessageType): String {
        return when (type) {
            is ClientMessageType.Register -> "register"
            is ClientMessageType.Withdraw -> "withdraw"
            is ClientMessageType.Reject -> "reject"
            is ClientMessageType.Acknowledge -> "acknowledge"
            is ClientMessageType.Message -> "message"
            is ClientMessageType.Status -> "status"
        }
    }

    @FromJson
    fun fromJson(type: String): ClientMessageType {
        return when (type) {
            "register" -> ClientMessageType.Register
            "withdraw" -> ClientMessageType.Withdraw
            "reject" -> ClientMessageType.Reject
            "acknowledge" -> ClientMessageType.Acknowledge
            "message" -> ClientMessageType.Message
            "status" -> ClientMessageType.Status
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }
}
