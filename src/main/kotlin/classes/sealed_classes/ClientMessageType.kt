package main.classes.sealed_classes

import com.squareup.moshi.Json
import data_classes.ClientRef
import data_classes.Topic
import main.data_classes.KKWQueueMessage
import main.util.MessageQueues

sealed class ClientMessageType {
    abstract fun exectue(queueMessage: KKWQueueMessage)
    abstract fun checkJson(json: String): Boolean
    @Json(name = "register")
    data object Register : ClientMessageType() {
        override fun exectue(queueMessage: KKWQueueMessage) {
            println("[Register Resolver] Registering ${queueMessage.clientOutgoingMessage.id}")

            val clientMessage = queueMessage.clientOutgoingMessage
            val clientSocket = queueMessage.clientSocket

            //checkJson()

            val topicName = clientMessage.topic!!
            val producerRef = ClientRef(clientMessage.id, clientSocket)

            val topic = Topic(producerRef, topicName, emptyList())

            MessageQueues.LT[topicName] = topic

            println("[Register Resolver] Registered" + MessageQueues.LT[topicName])
        }

        override fun checkJson(json: String): Boolean {
            return json.contains("register")
        }
    }

    @Json(name = "withdraw")
    data object Withdraw : ClientMessageType() {
        override fun exectue(queueMessage: KKWQueueMessage) {
            println("Withdrawing ${queueMessage.clientOutgoingMessage.id}")
        }

        override fun checkJson(json: String): Boolean {
            return json.contains("withdraw")
        }
    }

    @Json(name = "reject")
    data object Reject : ClientMessageType() {
        override fun exectue(queueMessage: KKWQueueMessage) {
            println("Rejecting ${queueMessage.clientOutgoingMessage.id}")
        }

        override fun checkJson(json: String): Boolean {
            return json.contains("reject")
        }
    }

    @Json(name = "acknowledge")
    data object Acknowledge : ClientMessageType() {
        override fun exectue(queueMessage: KKWQueueMessage) {
            println("Acknowledging ${queueMessage.clientOutgoingMessage.id}")
        }

        override fun checkJson(json: String): Boolean {
            return json.contains("acknowledge")
        }
    }

    @Json(name = "message")
    data object Message : ClientMessageType() {
        override fun exectue(queueMessage: KKWQueueMessage) {
            println("Sending message to ${queueMessage.clientOutgoingMessage.id}")
        }

        override fun checkJson(json: String): Boolean {
            return json.contains("message")
        }
    }

    @Json(name = "status")
    data object Status : ClientMessageType() {
        override fun exectue(queueMessage: KKWQueueMessage) {
            println("Checking status of ${queueMessage.clientOutgoingMessage.id}")
        }

        override fun checkJson(json: String): Boolean {
            return json.contains("status")
        }
    }
}