package classes.services

import main.adapters.JsonClientIncomingMessageAdapter
import main.classes.builders.ClientIncomingMessageBuilder
import main.classes.sealed_classes.ClientIncomingMessageMode
import classes.sealed_classes.ClientMessageType
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.sql.Timestamp

class Client {
    private lateinit var clientID: String
    private lateinit var reader: BufferedReader
    private lateinit var writer: PrintWriter
    private lateinit var socket: Socket

    private val jsonClientIncomingMessageAdapter = JsonClientIncomingMessageAdapter()

    fun start(serverIP: String, serverPort: Int, clientID: String) {
        this.clientID = clientID
        socket = Socket(serverIP, serverPort)
        reader = BufferedReader(InputStreamReader(socket.getInputStream()))
        writer = PrintWriter(socket.getOutputStream(), true)

        Thread {
            while (true) {
                val serverMessage = reader.readLine()
                if (serverMessage != null) {
                    println("Received message from server: $serverMessage")
                }
            }
        }.start()

        Thread {
            val consoleReader = BufferedReader(InputStreamReader(System.`in`))

            while (true) {
                val commandLine = consoleReader.readLine()
                val parts = commandLine.split(" ")
                val command = parts[0]
                val parameters = parts.drop(1)

                when (command) {
                    "isConnected" -> println(isConnected())
                    "getServerStatus" -> getServerStatus()
                    "getServerLogs" -> getServerLogs()
                    "createProducer" -> createProducer(parameters[0])
                    "produce" -> produce(parameters[0], parameters.drop(1).joinToString(" "))
                    "withdrawProducer" -> withdrawProducer(parameters[0])
                    "createSubscriber" -> createSubscriber(parameters[0])
                    "withdrawSubscriber" -> withdrawSubscriber(parameters[0])
                    else -> println("Unknown command: $command")
                }
            }
        }.start()

        println("Client $clientID is connected to server at $serverIP:$serverPort")
    }

    private fun isConnected(): Boolean {
        return socket.isConnected && !socket.isClosed
    }

    private fun getServerStatus() {
        val messageBuilder = ClientIncomingMessageBuilder()
            .setType(ClientMessageType.Status)
            .setTopic("logs")
            .setId(clientID)
            .setTimestamp(Timestamp(System.currentTimeMillis()))

        val message = messageBuilder.build()
        val jsonMessage = jsonClientIncomingMessageAdapter.toJson(message)
        writer.println(jsonMessage)
    }

    private fun getServerLogs() {
        val messageBuilder = ClientIncomingMessageBuilder()
//            .setType(ClientMessageType.GetLogs) Nie wiem jeszcze co ma robić ta metoda
            .setTopic("logs")
            .setId(clientID)
            .setTimestamp(Timestamp(System.currentTimeMillis()))

        val message = messageBuilder.build()
        val jsonMessage = jsonClientIncomingMessageAdapter.toJson(message)
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
    }
}