package classes.sealed_classes

import com.squareup.moshi.Json
import data_classes.ClientRef
import data_classes.Topic
import main.adapters.JsonClientOutgoingMessageAdapter
import main.classes.builders.ClientOutgoingMessageBuilder
import main.classes.sealed_classes.ClientIncomingMessageMode
import main.data_classes.ClientIncomingMessage
import main.data_classes.KKWQueueMessage
import main.util.MessageQueues
import main.util.ServerConfig
import java.io.PrintWriter
import java.sql.Timestamp

sealed class ClientMessageType {
    abstract fun execute(clientIncomingMessage: ClientIncomingMessage, clientRef: ClientRef)
    abstract fun checkJson(clientIncomingMessage: ClientIncomingMessage): Boolean

    @Json(name = "register")
    data object Register : ClientMessageType() {
        override fun execute(clientIncomingMessage: ClientIncomingMessage, clientRef: ClientRef) {
            println("[Register Callback] Register - from ${clientIncomingMessage.id}")

            if (clientIncomingMessage.mode == ClientIncomingMessageMode.Producer) {
                registerProducer(clientIncomingMessage, clientRef)
            } else {
                registerSubscriber(clientIncomingMessage, clientRef)
            }
        }

        override fun checkJson(clientIncomingMessage: ClientIncomingMessage): Boolean {
            return clientIncomingMessage.mode == ClientIncomingMessageMode.Producer
        }

        private fun registerProducer(clientIncomingMessage: ClientIncomingMessage, clientRef: ClientRef) {
            val topicName = clientIncomingMessage.topic!!
            val producerRef = ClientRef(clientIncomingMessage.id, clientRef.clientSocket)

            val topic = Topic(producerRef, topicName, mutableListOf())

            MessageQueues.LT[topicName] = topic

            println("[Register Callback] Registered Producer " + MessageQueues.LT[topicName])
        }

        private fun registerSubscriber(clientIncomingMessage: ClientIncomingMessage, clientRef: ClientRef) {
            val topicName = clientIncomingMessage.topic!!
            val subscriberRef = ClientRef(clientIncomingMessage.id, clientRef.clientSocket)

            val topic = MessageQueues.LT[topicName]
            topic?.subscribers?.add(subscriberRef)

            println("[Register Callback] Registered Subscriber " + MessageQueues.LT[topicName])
        }
    }

    @Json(name = "withdraw")
    data object Withdraw : ClientMessageType() {
        override fun execute(clientIncomingMessage: ClientIncomingMessage, clientRef: ClientRef) {
            println("[Withdraw Callback] Withdraw - from ${clientIncomingMessage.id}")

            //checkJson()
            if (clientIncomingMessage.mode == ClientIncomingMessageMode.Producer) {
                withdrawProducer(clientIncomingMessage)
            } else {
                withdrawSubscriber(clientIncomingMessage, clientRef)
            }
        }

        override fun checkJson(clientIncomingMessage: ClientIncomingMessage): Boolean {
            return clientIncomingMessage.mode == ClientIncomingMessageMode.Producer
        }

        private fun withdrawProducer(clientIncomingMessage: ClientIncomingMessage) {
            val topicName = clientIncomingMessage.topic!!
            MessageQueues.LT.remove(topicName)
            println("[Withdraw Callback] Withdraw Producer " + MessageQueues.LT[topicName])
        }

        private fun withdrawSubscriber(clientIncomingMessage: ClientIncomingMessage, clientRef: ClientRef) {
            val topicName = clientIncomingMessage.topic!!
            val subscriberRef = ClientRef(clientIncomingMessage.id, clientRef.clientSocket)

            val topic = MessageQueues.LT[topicName]
            topic?.subscribers?.remove(subscriberRef)
            println("[Withdraw Callback] Withdraw Subscriber " + MessageQueues.LT[topicName])
        }
    }

    @Json(name = "reject")
    data object Reject : ClientMessageType() {
        override fun execute(clientIncomingMessage: ClientIncomingMessage, clientRef: ClientRef) {
            println("[Reject Callback] Reject - from ${clientIncomingMessage.id}")
        }

        override fun checkJson(clientIncomingMessage: ClientIncomingMessage): Boolean {
            return clientIncomingMessage.mode == ClientIncomingMessageMode.Producer
        }
    }

    @Json(name = "acknowledge")
    data object Acknowledge : ClientMessageType() {
        private val jsonClientOutgoingMessageAdapter = JsonClientOutgoingMessageAdapter()

        override fun execute(clientIncomingMessage: ClientIncomingMessage, clientRef: ClientRef) {
            println("[Acknowledge Callback] Acknowledge - from ${clientIncomingMessage.id}")

            //checkJson
            val message = ClientOutgoingMessageBuilder()
                .copy(clientIncomingMessage)
                .build()

            println("[Acknowledge Callback] Acknowledge - to $message")

            val writer = PrintWriter(clientRef.clientSocket.getOutputStream(), true)
            val jsonMessage = jsonClientOutgoingMessageAdapter.toJson(message)
            writer.println(jsonMessage)
        }

        override fun checkJson(clientIncomingMessage: ClientIncomingMessage): Boolean {
            return clientIncomingMessage.mode == ClientIncomingMessageMode.Producer
        }
    }

    @Json(name = "message")
    data object Message : ClientMessageType() {
        override fun execute(clientIncomingMessage: ClientIncomingMessage, clientRef: ClientRef) {
            println("[Message Callback] Message - from ${clientIncomingMessage.id}")

            //checkJson()
            val topic = clientIncomingMessage.topic!!
            val incomingPayload = clientIncomingMessage.payload!!
            val subscribersOfTheTopic = MessageQueues.LT[topic]?.subscribers!!

            val sendAllMessage = ClientOutgoingMessageBuilder()
                .setId(ServerConfig.serverId)
                .setType(Acknowledge)
                .setTopic(topic)
                .setTimestamp(Timestamp(System.currentTimeMillis()))
                .setPayload(
                    mapOf(
                        "message" to incomingPayload["message"]!!,
                    )
                ).build()

            val logMessage = ClientOutgoingMessageBuilder()
                .setId(ServerConfig.serverId)
                .setType(Acknowledge)
                .setTopic("logs")
                .setTimestamp(Timestamp(System.currentTimeMillis()))
                .setPayload(
                    mapOf(
                        "timestampOfMessage" to clientIncomingMessage.timestamp,
                        "topicOfMessage" to clientIncomingMessage.topic,
                        "success" to true,
                        "message" to "Message was resent to ${subscribersOfTheTopic.size} subscribers"
                    )
                ).build()

            MessageQueues.KKW.add(KKWQueueMessage(sendAllMessage, subscribersOfTheTopic))
            MessageQueues.KKW.add(KKWQueueMessage(logMessage, listOf(clientRef)))
        }

        override fun checkJson(clientIncomingMessage: ClientIncomingMessage): Boolean {
            return clientIncomingMessage.mode == ClientIncomingMessageMode.Producer
        }
    }

    @Json(name = "status")
    data object Status : ClientMessageType() {
        override fun execute(clientIncomingMessage: ClientIncomingMessage, clientRef: ClientRef) {
            println("[Status Callback] Status - from ${clientIncomingMessage.id}")
        }

        override fun checkJson(clientIncomingMessage: ClientIncomingMessage): Boolean {
            return clientIncomingMessage.mode == ClientIncomingMessageMode.Producer
        }
    }
}