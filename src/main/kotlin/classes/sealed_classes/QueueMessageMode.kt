package main.classes.sealed_classes

import com.squareup.moshi.Json

sealed class QueueMessageMode {
    @Json(name = "producer")
    object Producer : QueueMessageMode()

    @Json(name = "subscriber")
    object Subscriber : QueueMessageMode()
}
