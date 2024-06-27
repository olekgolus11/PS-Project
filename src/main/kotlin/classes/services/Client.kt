package classes.services

import main.adapters.JsonClientIncomingMessageAdapter
import main.classes.builders.ClientIncomingMessageBuilder
import main.classes.sealed_classes.ClientIncomingMessageMode
import classes.sealed_classes.ClientMessageType
import main.adapters.JsonClientOutgoingMessageAdapter
import main.data_classes.ClientIncomingMessage
import main.data_classes.ClientOutgoingMessage
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.sql.Timestamp

class Client {
    private lateinit var clientID: String
    private lateinit var reader: BufferedReader
    private lateinit var writer: PrintWriter
    private lateinit var consoleReader: BufferedReader
    private lateinit var socket: Socket

    private var isClientRunning = true
    private val jsonClientIncomingMessageAdapter = JsonClientIncomingMessageAdapter()
    private val jsonClientOutgoingMessageAdapter = JsonClientOutgoingMessageAdapter()
    private val sentMessages = mutableListOf<ClientIncomingMessage>()
    private var getServerLogsCallback: ((ClientOutgoingMessage) -> Unit)? = null
    private var getServerStatusCallback: ((ClientOutgoingMessage) -> Unit)? = null


    fun start(serverIP: String, serverPort: Int, clientID: String) {
        this.clientID = clientID
        socket = Socket(serverIP, serverPort)
        reader = BufferedReader(InputStreamReader(socket.getInputStream()))
        writer = PrintWriter(socket.getOutputStream(), true)
        consoleReader = BufferedReader(InputStreamReader(System.`in`))

        Thread {
            while (isClientRunning) {
                val serverMessage = reader.readLine()
                if (serverMessage != null) {
                    val message = jsonClientOutgoingMessageAdapter.fromJson(serverMessage)

                    if (message.payload?.get("logs") != null) {
                        getServerLogsCallback?.invoke(message)
                    } else if (message.payload?.get("topics") != null) {
                        getServerStatusCallback?.invoke(message)
                    } else {
                        println("Received message: $message")
                    }
                    if (message.payload?.get("message") == "Producer has withdrawn from the topic") {
                        println("You have been withdrawn from the only topic you have subscribed to, stopping client")
                        stop()
                    }
                } else {
                    println("Stopping client")
                    stop()
                    break
                }
            }
            println("Stopped receiving messages from server")
        }.start()

        Thread {
            while (isClientRunning) {
                val commandLine = consoleReader.readLine() ?: break
                val parts = commandLine.split(" ")
                val command = parts[0]
                val parameters = parts.drop(1)

                try {
                    when (command) {
                        "isConnected" -> println(isConnected())
                        "produce" -> produce(parameters[0], parameters.drop(1).joinToString(" "))
                        "createProducer" -> createProducer(parameters[0])
                        "withdrawProducer" -> withdrawProducer(parameters[0])
                        "createSubscriber" -> createSubscriber(parameters[0])
                        "withdrawSubscriber" -> withdrawSubscriber(parameters[0])
                        "getStatus" -> getStatus()
                        "getServerStatus" -> getServerStatus(::printStatusCallback)
                        "getServerLogs" -> getServerLogs(::printLogsCallback)
                        "stop" -> {
                            stop()
                            break
                        }

                        else -> println("Unknown command: $command")
                    }
                } catch (e: Exception) {
                    println("You must have entered the wrong parameters, please try again")
                }
            }
            println("Stopped sending messages to server")
        }.start()

        println("Client $clientID is connected to server at $serverIP:$serverPort")
    }

    private fun isConnected(): Boolean {
        return socket.isConnected && !socket.isClosed
    }

    private fun getStatus() {
        val messageBuilder = ClientIncomingMessageBuilder()
            .setType(ClientMessageType.Status)
            .setTopic("logs")
            .setId(clientID)
            .setTimestamp(Timestamp(System.currentTimeMillis()))
            .setPayload(
                mapOf(
                    "message" to "GetStatus"
                )
            )

        val message = messageBuilder.build()
        val jsonMessage = jsonClientIncomingMessageAdapter.toJson(message)
        writer.println(jsonMessage)
        sentMessages.add(message)
    }

    private fun getServerStatus(callback: (ClientOutgoingMessage) -> Unit) {
        val messageBuilder = ClientIncomingMessageBuilder()
            .setType(ClientMessageType.Status)
            .setTopic("logs")
            .setId(clientID)
            .setTimestamp(Timestamp(System.currentTimeMillis()))
            .setPayload(
                mapOf(
                    "message" to "GetServerStatus"
                )
            )

        val message = messageBuilder.build()
        val jsonMessage = jsonClientIncomingMessageAdapter.toJson(message)
        this.getServerStatusCallback = callback
        writer.println(jsonMessage)
        sentMessages.add(message)
    }

    private fun printStatusCallback(clientOutgoingMessage: ClientOutgoingMessage) {
        val topics = clientOutgoingMessage.payload?.get("topics") as? List<*>
        val message = clientOutgoingMessage.payload?.get("message") as? String
        println(message)
        topics?.forEach { topic ->
            println("Topic: $topic")
        }
    }

    private fun printLogsCallback(clientOutgoingMessage: ClientOutgoingMessage) {
        println("Messages sent by client: $sentMessages")
        println("Logs from server:")
        val logs = clientOutgoingMessage.payload?.get("logs") as? List<*>
        logs?.forEach { log ->
            println("Log: $log")
        }
    }

    private fun getServerLogs(callback: (ClientOutgoingMessage) -> Unit) {
        val messageBuilder = ClientIncomingMessageBuilder()
            .setType(ClientMessageType.Status)
            .setTopic("logs")
            .setId(clientID)
            .setTimestamp(Timestamp(System.currentTimeMillis()))
            .setPayload(
                mapOf(
                    "message" to "GetServerLogs"
                )
            )

        val message = messageBuilder.build()
        val jsonMessage = jsonClientIncomingMessageAdapter.toJson(message)
        this.getServerLogsCallback = callback
        writer.println(jsonMessage)
    }

    private fun createProducer(topic: String) {
        val messageBuilder = ClientIncomingMessageBuilder()
            .setType(ClientMessageType.Register)
            .setTopic(topic)
            .setId(clientID)
            .setMode(ClientIncomingMessageMode.Producer)
            .setTimestamp(Timestamp(System.currentTimeMillis()))

        val message = messageBuilder.build()
        val jsonMessage = jsonClientIncomingMessageAdapter.toJson(message)
        writer.println(jsonMessage)
        sentMessages.add(message)
    }

    private fun withdrawProducer(topic: String) {
        val messageBuilder = ClientIncomingMessageBuilder()
            .setType(ClientMessageType.Withdraw)
            .setTopic(topic)
            .setId(clientID)
            .setMode(ClientIncomingMessageMode.Producer)
            .setTimestamp(Timestamp(System.currentTimeMillis()))

        val message = messageBuilder.build()
        val jsonMessage = jsonClientIncomingMessageAdapter.toJson(message)
        writer.println(jsonMessage)
        sentMessages.add(message)
    }

    private fun createSubscriber(topic: String) {
        val messageBuilder = ClientIncomingMessageBuilder()
            .setType(ClientMessageType.Register)
            .setTopic(topic)
            .setId(clientID)
            .setMode(ClientIncomingMessageMode.Subscriber)
            .setTimestamp(Timestamp(System.currentTimeMillis()))

        val message = messageBuilder.build()
        val jsonMessage = jsonClientIncomingMessageAdapter.toJson(message)
        writer.println(jsonMessage)
        sentMessages.add(message)
    }

    private fun withdrawSubscriber(topic: String) {
        val messageBuilder = ClientIncomingMessageBuilder()
            .setType(ClientMessageType.Withdraw)
            .setTopic(topic)
            .setId(clientID)
            .setMode(ClientIncomingMessageMode.Subscriber)
            .setTimestamp(Timestamp(System.currentTimeMillis()))

        val message = messageBuilder.build()
        val jsonMessage = jsonClientIncomingMessageAdapter.toJson(message)
        writer.println(jsonMessage)
        sentMessages.add(message)
    }

    private fun produce(topic: String, payload: String) {
        val messageBuilder = ClientIncomingMessageBuilder()
            .setType(ClientMessageType.Message)
            .setTopic(topic)
            .setId(clientID)
            .setTimestamp(Timestamp(System.currentTimeMillis()))
            .setMode(ClientIncomingMessageMode.Producer)
            .setPayload(mapOf("message" to payload))

        val message = messageBuilder.build()
        val jsonMessage = jsonClientIncomingMessageAdapter.toJson(message)
        writer.println(jsonMessage)
        sentMessages.add(message)
    }

    private fun stop() {
        if (isClientRunning) {
            isClientRunning = false
            socket.shutdownInput()
            socket.shutdownOutput()
            reader.close()
            writer.close()
            socket.close()
            System.`in`.close()
        }
    }
}