package data_classes

import java.net.Socket

data class ClientRef(
    val clientID: String,
    val clientSocket: Socket
)
