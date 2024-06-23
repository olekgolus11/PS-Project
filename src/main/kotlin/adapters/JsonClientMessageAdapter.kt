package main.adapters

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import main.data_classes.ClientMessage

class JsonClientMessageAdapter {
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val adapter: JsonAdapter<ClientMessage> = moshi.adapter(ClientMessage::class.java)

    fun toJson(clientMessage: ClientMessage): String {
        return adapter.toJson(clientMessage)
    }

    fun fromJson(json: String): ClientMessage {
        return adapter.fromJson(json)!!
    }
}