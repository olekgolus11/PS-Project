package main.data_classes

import java.net.Socket

data class KKOQueueMessage(
    val clientIncomingMessage: String,
    val clientSocket: Socket
)
