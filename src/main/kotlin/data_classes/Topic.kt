package data_classes

import java.net.Socket

data class Topic(
    val producerRef: ClientRef,
    val topicName: String,
    val subscribers: MutableList<ClientRef>
)
