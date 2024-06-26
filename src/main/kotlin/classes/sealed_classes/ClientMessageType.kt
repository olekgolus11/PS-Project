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
    open fun checkJson(clientIncomingMessage: ClientIncomingMessage, clientRef: ClientRef) {
        if (clientIncomingMessage.id == null) {
            throw IllegalArgumentException("ID cannot be null")
        }
        if (clientIncomingMessage.type == null) {
            throw IllegalArgumentException("Type cannot be null")
        }
        if (clientIncomingMessage.timestamp == null) {
            throw IllegalArgumentException("Timestamp cannot be null")
        }
    }

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

        override fun checkJson(clientIncomingMessage: ClientIncomingMessage, clientRef: ClientRef) {
            if (clientIncomingMessage.mode == null) {
                throw IllegalArgumentException("Mode cannot be null")
            }
            if (clientIncomingMessage.topic == null) {
                throw IllegalArgumentException("Topic cannot be null")
            }

            if (clientIncomingMessage.mode == ClientIncomingMessageMode.Producer) {
                if (MessageQueues.LT.containsKey(clientIncomingMessage.topic)) {
                    throw IllegalArgumentException("Topic already exists")
                }
            }
            if (clientIncomingMessage.mode == ClientIncomingMessageMode.Subscriber) {
                if (!MessageQueues.LT.containsKey(clientIncomingMessage.topic)) {
                    throw IllegalArgumentException("Topic does not exist")
                }
                if (MessageQueues.LT[clientIncomingMessage.topic]?.subscribers?.contains(clientRef) == true) {
                    throw IllegalArgumentException("Subscriber already exists is this topic")
                }
            }
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

            if (clientIncomingMessage.mode == ClientIncomingMessageMode.Producer) {
                withdrawProducer(clientIncomingMessage)
            } else {
                withdrawSubscriber(clientIncomingMessage, clientRef)
            }
        }

        override fun checkJson(clientIncomingMessage: ClientIncomingMessage, clientRef: ClientRef) {
            if (clientIncomingMessage.mode == null) {
                throw IllegalArgumentException("Mode cannot be null")
            }
            if (clientIncomingMessage.topic == null) {
                throw IllegalArgumentException("Topic cannot be null")
            }

            if (clientIncomingMessage.mode == ClientIncomingMessageMode.Producer) {
                if (!MessageQueues.LT.containsKey(clientIncomingMessage.topic)) {
                    throw IllegalArgumentException("Topic does not exist")
                }
                if (MessageQueues.LT[clientIncomingMessage.topic]?.producerRef != clientRef) {
                    throw IllegalArgumentException("Client is not the producer of this topic")
                }
            }
            if (clientIncomingMessage.mode == ClientIncomingMessageMode.Subscriber) {
                if (!MessageQueues.LT.containsKey(clientIncomingMessage.topic)) {
                    throw IllegalArgumentException("Topic does not exist")
                }
                if (MessageQueues.LT[clientIncomingMessage.topic]?.subscribers?.contains(clientRef) == false) {
                    throw IllegalArgumentException("Subscriber does not exist in this topic")
                }
            }
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
        private val jsonClientOutgoingMessageAdapter = JsonClientOutgoingMessageAdapter()

        override fun execute(clientIncomingMessage: ClientIncomingMessage, clientRef: ClientRef) {
            println("[Reject Callback] Reject - from ${clientIncomingMessage.id}")

            val message = ClientOutgoingMessageBuilder()
                .copy(clientIncomingMessage)
                .build()

            println("[Reject Callback] Reject - to $message")

            val writer = PrintWriter(clientRef.clientSocket.getOutputStream(), true)
            val jsonMessage = jsonClientOutgoingMessageAdapter.toJson(message)
            writer.println(jsonMessage)
        }

        override fun checkJson(clientIncomingMessage: ClientIncomingMessage, clientRef: ClientRef) {
            if (clientIncomingMessage.payload == null) {
                throw IllegalArgumentException("Payload cannot be null")
            }
            if (clientIncomingMessage.payload["message"] == null) {
                throw IllegalArgumentException("Message cannot be null")
            }
            if (clientIncomingMessage.payload["success"] == null) {
                throw IllegalArgumentException("Success cannot be null")
            }
            if (clientIncomingMessage.payload["timestampOfMessage"] == null) {
                throw IllegalArgumentException("Timestamp Of Message cannot be null")
            }
            if (clientIncomingMessage.payload["topicOfMessage"] == null) {
                throw IllegalArgumentException("Topic Of Message cannot be null")
            }
        }
    }

    @Json(name = "acknowledge")
    data object Acknowledge : ClientMessageType() {
        private val jsonClientOutgoingMessageAdapter = JsonClientOutgoingMessageAdapter()

        override fun execute(clientIncomingMessage: ClientIncomingMessage, clientRef: ClientRef) {
            println("[Acknowledge Callback] Acknowledge - from ${clientIncomingMessage.id}")

            val message = ClientOutgoingMessageBuilder()
                .copy(clientIncomingMessage)
                .build()

            println("[Acknowledge Callback] Acknowledge - to $message")

            val writer = PrintWriter(clientRef.clientSocket.getOutputStream(), true)
            val jsonMessage = jsonClientOutgoingMessageAdapter.toJson(message)
            writer.println(jsonMessage)
        }

        override fun checkJson(clientIncomingMessage: ClientIncomingMessage, clientRef: ClientRef) {
            if (clientIncomingMessage.payload == null) {
                throw IllegalArgumentException("Payload cannot be null")
            }
            if (clientIncomingMessage.payload["message"] == null) {
                throw IllegalArgumentException("Message cannot be null")
            }
            if (clientIncomingMessage.payload["success"] == null) {
                throw IllegalArgumentException("Success cannot be null")
            }
            if (clientIncomingMessage.payload["timestampOfMessage"] == null) {
                throw IllegalArgumentException("Timestamp Of Message cannot be null")
            }
            if (clientIncomingMessage.payload["topicOfMessage"] == null) {
                throw IllegalArgumentException("Topic Of Message cannot be null")
            }
        }
    }

    @Json(name = "message")
    data object Message : ClientMessageType() {
        override fun execute(clientIncomingMessage: ClientIncomingMessage, clientRef: ClientRef) {
            println("[Message Callback] Message - from ${clientIncomingMessage.id}")

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

        override fun checkJson(clientIncomingMessage: ClientIncomingMessage, clientRef: ClientRef) {
            if (clientIncomingMessage.mode == ClientIncomingMessageMode.Subscriber) {
                throw IllegalArgumentException("Mode cannot be Subscriber")
            }
            if (MessageQueues.LT[clientIncomingMessage.topic]?.producerRef != clientRef) {
                throw IllegalArgumentException("Producer is not the same as the client")
            }
            if (clientIncomingMessage.payload == null) {
                throw IllegalArgumentException("Payload cannot be null")
            }
            if (clientIncomingMessage.payload["message"] == null) {
                throw IllegalArgumentException("Message cannot be null")
            }
            if (MessageQueues.LT[clientIncomingMessage.topic]?.subscribers == null || MessageQueues.LT[clientIncomingMessage.topic]?.subscribers?.isEmpty() == true) {
                throw IllegalArgumentException("Subscribers cannot be empty")
            }
        }
    }

    @Json(name = "status")
    data object Status : ClientMessageType() {
        override fun execute(clientIncomingMessage: ClientIncomingMessage, clientRef: ClientRef) {
            println("[Status Callback] Status - from ${clientIncomingMessage.id}")
        }

        override fun checkJson(clientIncomingMessage: ClientIncomingMessage, clientRef: ClientRef) {
        }
    }
}