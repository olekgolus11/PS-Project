package main.data_classes

import data_classes.ClientRef

data class KKWQueueMessage(
    val clientOutgoingMessage: ClientOutgoingMessage,
    val clientRefs: List<ClientRef>
)
