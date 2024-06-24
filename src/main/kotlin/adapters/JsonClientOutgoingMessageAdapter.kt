package main.adapters

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import main.data_classes.ClientIncomingMessage
import main.data_classes.ClientOutgoingMessage

class JsonClientOutgoingMessageAdapter {
    private val moshi: Moshi = Moshi.Builder()
        .add(TimestampAdapter())
        .add(JsonClientMessageTypeAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()

    private val adapter: JsonAdapter<ClientOutgoingMessage> = moshi.adapter(ClientOutgoingMessage::class.java)

    fun toJson(clientOutgoingMessage: ClientOutgoingMessage): String {
        return adapter.toJson(clientOutgoingMessage)
    }

    fun fromJson(json: String): ClientOutgoingMessage {
        return adapter.fromJson(json)!!
    }
}