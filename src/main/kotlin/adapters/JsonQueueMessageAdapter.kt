package main.adapters

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import main.data_classes.QueueMessage

class JsonQueueMessageAdapter {
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val adapter: JsonAdapter<QueueMessage> = moshi.adapter(QueueMessage::class.java)

    fun toJson(queueMessage: QueueMessage): String {
        return adapter.toJson(queueMessage)
    }

    fun fromJson(json: String): QueueMessage {
        return adapter.fromJson(json)!!
    }
}