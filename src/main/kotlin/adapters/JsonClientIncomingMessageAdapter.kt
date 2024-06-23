package main.adapters

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import main.data_classes.ClientIncomingMessage

class JsonClientIncomingMessageAdapter {
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val adapter: JsonAdapter<ClientIncomingMessage> = moshi.adapter(ClientIncomingMessage::class.java)

    fun toJson(clientIncomingMessage: ClientIncomingMessage): String {
        return adapter.toJson(clientIncomingMessage)
    }

    fun fromJson(json: String): ClientIncomingMessage {
        return adapter.fromJson(json)!!
    }
}