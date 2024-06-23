package main.data_classes

import java.net.Socket

data class KKOQueueMessage(
    val clientMessage: ClientMessage,
    val clientSocket: Socket
)
