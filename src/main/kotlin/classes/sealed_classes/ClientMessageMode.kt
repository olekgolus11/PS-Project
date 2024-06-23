package main.classes.sealed_classes

import com.squareup.moshi.Json

sealed class ClientMessageMode {
    @Json(name = "producer")
    object Producer : ClientMessageMode()

    @Json(name = "subscriber")
    object Subscriber : ClientMessageMode()
}
