package main.adapters

import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import main.classes.sealed_classes.ClientIncomingMessageMode

class JsonClientIncomingMessageModeAdapter {
    @ToJson
    fun toJson(mode: ClientIncomingMessageMode): String {
        return when (mode) {
            is ClientIncomingMessageMode.Producer -> "producer"
            is ClientIncomingMessageMode.Subscriber -> "subscriber"
        }
    }

    @FromJson
    fun fromJson(mode: String): ClientIncomingMessageMode {
        return when (mode) {
            "producer" -> ClientIncomingMessageMode.Producer
            "subscriber" -> ClientIncomingMessageMode.Subscriber
            else -> throw IllegalArgumentException("Unknown mode: $mode")
        }
    }
}