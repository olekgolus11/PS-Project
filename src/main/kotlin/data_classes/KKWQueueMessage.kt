package main.data_classes

import java.net.Socket

data class KKWQueueMessage(
    val clientOutgoingMessage: ClientOutgoingMessage,
    val clientSocket: Socket
)
