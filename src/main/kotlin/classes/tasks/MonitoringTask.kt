package main.classes.tasks

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import data_classes.ClientRef
import main.adapters.JsonClientIncomingMessageAdapter
import main.classes.builders.ClientIncomingMessageBuilder
import classes.sealed_classes.ClientMessageType
import main.classes.builders.ClientOutgoingMessageBuilder
import main.data_classes.KKWQueueMessage
import main.interfaces.ServerTask
import main.util.MessageQueues
import main.util.ServerConfig
import java.sql.Timestamp

class MonitoringTask : ServerTask {
    private var isRunning = true
    private val jsonClientIncomingMessageAdapter = JsonClientIncomingMessageAdapter()
    private val jsonClientIncomingMessageErrorAdapter: JsonAdapter<Map<String, Any>> = Moshi.Builder().build().adapter(
        Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java))

    override fun run() {
        println("[Monitoring] Monitoring task is running")

        while (isRunning) {
            if (MessageQueues.KKO.isEmpty()) {
                Thread.sleep(1)
                continue
            }

            val kkoQueueMessage = MessageQueues.KKO.poll()
            val clientMessage = kkoQueueMessage.clientIncomingMessage
            val clientSocket = kkoQueueMessage.clientSocket

            try {
                println("[Monitoring] Parsing message")

                val parsedClientMessage = jsonClientIncomingMessageAdapter.fromJson(clientMessage)
                val clientID = parsedClientMessage.id
                val clientRef = ClientRef(clientID, clientSocket)

                parsedClientMessage.type.checkJson(parsedClientMessage, clientRef)

                println("[Monitoring] Received message: $parsedClientMessage")

                parsedClientMessage.type.execute(parsedClientMessage, clientRef)
            } catch (e: Exception) {
                println("[Monitoring] Failed to parse message: $clientMessage, error: ${e.message}")

                val messageMap = jsonClientIncomingMessageErrorAdapter.fromJson(clientMessage)
                val timestamp = messageMap?.get("timestamp") as? String
                val topic = messageMap?.get("topic") as? String
                val clientID = messageMap?.get("id") as? String ?: "Unknown"
                val clientRef = ClientRef(clientID, clientSocket)

                val payload = mapOf(
                    "timestampOfMessage" to (timestamp ?: "N/A"),
                    "topicOfMessage" to (topic ?: "N/A"),
                    "success" to false,
                    "message" to e.message!!
                )

                val clientOutgoingMessage = ClientOutgoingMessageBuilder()
                    .setType(ClientMessageType.Reject)
                    .setId(ServerConfig.serverId)
                    .setTopic("logs")
                    .setTimestamp(Timestamp(System.currentTimeMillis()))
                    .setPayload(payload)

                val clientIncomingMessage = clientOutgoingMessage.build()

                MessageQueues.KKW.add(KKWQueueMessage(clientIncomingMessage, listOf(clientRef)))
            }
        }
    }

    override fun stop() {
        isRunning = false
        println("[Monitoring] Monitoring task is stopping")
    }
}