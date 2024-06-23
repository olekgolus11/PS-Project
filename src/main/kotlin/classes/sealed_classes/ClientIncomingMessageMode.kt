package main.classes.sealed_classes

import com.squareup.moshi.Json

sealed class ClientIncomingMessageMode {
    @Json(name = "producer")
    data object Producer : ClientIncomingMessageMode()

    @Json(name = "subscriber")
    data object Subscriber : ClientIncomingMessageMode()
}
