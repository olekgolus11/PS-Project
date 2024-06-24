package classes.sealed_classes

import com.squareup.moshi.Json
import data_classes.ClientRef
import data_classes.Topic
import main.adapters.JsonClientOutgoingMessageAdapter
import main.classes.builders.ClientOutgoingMessageBuilder
import main.data_classes.ClientIncomingMessage
import main.data_classes.ClientOutgoingMessage
import main.data_classes.KKWQueueMessage
import main.util.MessageQueues
import main.util.ServerConfig
import java.io.PrintWriter
import java.sql.Timestamp

sealed class ClientMessageType {
    abstract fun exectue(clientIncomingMessage: ClientIncomingMessage, clientRef: ClientRef)
    abstract fun checkJson(json: String): Boolean

    @Json(name = "register")
    data object Register : ClientMessageType() {
        override fun exectue(clientIncomingMessage: ClientIncomingMessage, clientRef: ClientRef) {
            println("[Register Callback] Register - from ${clientIncomingMessage.id}")

            //checkJson()
            val topicName = clientIncomingMessage.topic!!
            val producerRef = ClientRef(clientIncomingMessage.id, clientRef.clientSocket)

            val topic = Topic(producerRef, topicName, emptyList())

            MessageQueues.LT[topicName] = topic

            println("[Register Callback] Registered" + MessageQueues.LT[topicName])
        }

        override fun checkJson(json: String): Boolean {
            return json.contains("register")
        }
    }

    @Json(name = "withdraw")
    data object Withdraw : ClientMessageType() {
        override fun exectue(clientIncomingMessage: ClientIncomingMessage, clientRef: ClientRef) {
            println("[Withdraw Callback] Withdraw - from ${clientIncomingMessage.id}")
        }

        override fun checkJson(json: String): Boolean {
            return json.contains("withdraw")
        }
    }

    @Json(name = "reject")
    data object Reject : ClientMessageType() {
        override fun exectue(clientIncomingMessage: ClientIncomingMessage, clientRef: ClientRef) {
            println("[Reject Callback] Reject - from ${clientIncomingMessage.id}")
        }

        override fun checkJson(json: String): Boolean {
            return json.contains("reject")
        }
    }

    @Json(name = "acknowledge")
    data object Acknowledge : ClientMessageType() {
        private val jsonClientOutgoingMessageAdapter = JsonClientOutgoingMessageAdapter()

        override fun exectue(clientIncomingMessage: ClientIncomingMessage, clientRef: ClientRef) {
            println("[Acknowledge Callback] Acknowledge - from ${clientIncomingMessage.id}")

            //checkJson
            val message = ClientOutgoingMessageBuilder()
                .copy(clientIncomingMessage)
                .build()

            val writer = PrintWriter(clientRef.clientSocket.getOutputStream(), true)
            val jsonMessage = jsonClientOutgoingMessageAdapter.toJson(message)
            writer.println(jsonMessage)
        }

        override fun checkJson(json: String): Boolean {
            return json.contains("acknowledge")
        }
    }

    @Json(name = "message")
    data object Message : ClientMessageType() {
        override fun exectue(clientIncomingMessage: ClientIncomingMessage, clientRef: ClientRef) {
            println("[Message Callback] Message - from ${clientIncomingMessage.id}")

            //checkJson()
            val topic = clientIncomingMessage.topic!!
            val incomingPayload = clientIncomingMessage.payload!!
            val subscribersOfTheTopic = MessageQueues.LT[topic]?.subscribers

            val sendAllMessage = ClientOutgoingMessageBuilder()
                .setId(ServerConfig.serverId)
                .setType(Acknowledge)
                .setTopic(topic)
                .setTimestamp(Timestamp(System.currentTimeMillis()))
                .setPayload(
                    mapOf(
                        "message" to incomingPayload["message"]!!,
                        "subscribers" to subscribersOfTheTopic!!
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

            MessageQueues.KKW.add(KKWQueueMessage(sendAllMessage, listOf(clientRef)))
            MessageQueues.KKW.add(KKWQueueMessage(logMessage, listOf(clientRef)))
        }

        override fun checkJson(json: String): Boolean {
            return json.contains("message")
        }
    }

    @Json(name = "status")
    data object Status : ClientMessageType() {
        override fun exectue(clientIncomingMessage: ClientIncomingMessage, clientRef: ClientRef) {
            println("[Status Callback] Status - from ${clientIncomingMessage.id}")
        }

        override fun checkJson(json: String): Boolean {
            return json.contains("status")
        }
    }
}