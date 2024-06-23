package main.classes.tasks

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import main.adapters.JsonClientIncomingMessageAdapter
import main.classes.builders.ClientOutgoingMessageBuilder
import main.classes.sealed_classes.ClientMessageType
import main.data_classes.KKWQueueMessage
import main.interfaces.ServerTask
import main.util.MessageQueues
import main.util.ServerConfig
import java.sql.Timestamp

class MonitoringTask : ServerTask {
    private var stopMonitoring = false
    private val jsonClientIncomingMessageAdapter = JsonClientIncomingMessageAdapter()
    private val jsonClientIncomingMessageErrorAdapter: JsonAdapter<Map<String, Any>> = Moshi.Builder().build().adapter(
        Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java))

    override fun run() {
        println("[Monitoring] Monitoring task is running")

        while (!stopMonitoring) {
            if (MessageQueues.KKO.isEmpty()) {
                Thread.sleep(1)
                continue
            }

            val kkoQueueMessage = MessageQueues.KKO.poll()
            val clientMessage = kkoQueueMessage.clientIncomingMessage
            val clientSocket = kkoQueueMessage.clientSocket

            try {
                val parsedClientMessage = jsonClientIncomingMessageAdapter.fromJson(clientMessage)
                println("[Monitoring] Received message: $parsedClientMessage")
            } catch (e: Exception) {
                println("[Monitoring] Failed to parse message: $clientMessage")

                val messageMap = jsonClientIncomingMessageErrorAdapter.fromJson(clientMessage)
                val timestamp = messageMap?.get("timestamp") as? Timestamp
                val topic = messageMap?.get("topic") as? String

                val payload = mapOf(
                    "timestampOfMessage" to (timestamp ?: "N/A"),
                    "topicOfMessage" to (topic ?: "N/A"),
                    "success" to false,
                    "message" to "The message was unpleasant"
                )

                val clientOutgoingMessageBuilder = ClientOutgoingMessageBuilder()
                    .setType(ClientMessageType.Reject)
                    .setId(ServerConfig.serverId)
                    .setTopic("logs")
                    .setTimestamp(Timestamp(System.currentTimeMillis()))
                    .setPayload(payload)

                val clientOutgoingMessage = clientOutgoingMessageBuilder.build()
                val kkwQueueMessage = KKWQueueMessage(clientOutgoingMessage, clientSocket)
                MessageQueues.KKW.add(kkwQueueMessage)
            }
        }
    }

    override fun stop() {
        println("Monitoring task is stopping")
    }
}