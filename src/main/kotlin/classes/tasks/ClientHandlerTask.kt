package main.classes.tasks

import classes.sealed_classes.ClientMessageType
import main.adapters.JsonClientIncomingMessageAdapter
import main.classes.builders.ClientIncomingMessageBuilder
import main.classes.builders.ClientOutgoingMessageBuilder
import main.classes.sealed_classes.ClientIncomingMessageMode
import main.data_classes.ClientIncomingMessage
import main.data_classes.KKOQueueMessage
import main.data_classes.KKWQueueMessage
import main.interfaces.ServerTask
import main.util.MessageQueues
import java.net.Socket
import main.util.ServerConfig
import java.net.SocketException
import java.net.SocketTimeoutException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.sql.Timestamp

class ClientHandlerTask(private val clientSocket: Socket) : ServerTask {
    private var stopClient = false
    private var jsonClientIncomingMessageAdapter = JsonClientIncomingMessageAdapter()

    override fun run() {
        println("[Client Handler] task is running")

        clientSocket.soTimeout = ServerConfig.timeOut * 1000
        val reader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))

        while (!stopClient) {
            try {
                val message = reader.readLine()
                if (message == null) {
                    println("[Client Handler] End of stream reached, stopping task")
                    stop()
                    break
                }
                val kkoQueueMessage = KKOQueueMessage(message, clientSocket)
                MessageQueues.KKO.add(kkoQueueMessage)
            } catch (e: SocketTimeoutException) {
                println("[Client Handler] Socket timeout")
            } catch (e: SocketException) {
                println("[Client Handler] Socket closed")
                stop()
            }
        }
    }

    override fun stop() {
        stopClient = true
        clientSocket.shutdownInput()
        clientSocket.close()
        removeClientFromSubscribers()
        removeTopicIfClientIsProducer()
        println("[Client Handler] Client Handler task stopped")
    }

    private fun removeClientFromSubscribers() {
        MessageQueues.LT.forEach { (_, topic) ->
            topic.subscribers.removeIf { it.clientSocket == clientSocket }
        }
    }

    private fun removeTopicIfClientIsProducer() {
        MessageQueues.LT.entries.forEach { (topicName, topic) ->
            val isProducer = topic.producerRef?.clientSocket == clientSocket
            if (isProducer) {
                val withdrawMessage = ClientIncomingMessageBuilder()
                    .setId(topic.producerRef!!.clientID)
                    .setType(ClientMessageType.Withdraw)
                    .setMode(ClientIncomingMessageMode.Producer)
                    .setTopic(topicName)
                    .setTimestamp(Timestamp(System.currentTimeMillis()))
                    .setPayload(
                        mapOf(
                            "success" to true,
                            "message" to "Producer has withdrawn from the topic"
                        )
                    ).build()

                val withdrawMessageAsJson = jsonClientIncomingMessageAdapter.toJson(withdrawMessage)

                MessageQueues.KKO.add(KKOQueueMessage(withdrawMessageAsJson, topic.producerRef.clientSocket))
            }
        }
    }
}