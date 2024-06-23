package main.classes.sealed_classes

import com.squareup.moshi.Json

sealed class ClientIncomingMessageMode {
    @Json(name = "producer")
    object Producer : ClientIncomingMessageMode()

    @Json(name = "subscriber")
    object Subscriber : ClientIncomingMessageMode()
}
