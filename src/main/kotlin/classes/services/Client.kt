package main.classes.services

import main.adapters.JsonClientIncomingMessageAdapter
import main.classes.builders.ClientIncomingMessageBuilder
import main.classes.sealed_classes.ClientIncomingMessageMode
import main.classes.sealed_classes.ClientMessageType
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
                val command = consoleReader.readLine()
                when (command) {
                    "isConnected" -> println(isConnected())
                    "getServerStatus" -> getServerStatus()
                    else -> println("Unknown command: $command")
                }
            }
        }.start()

        println("Client $clientID is connected to server at $serverIP:$serverPort")
    }

    fun isConnected(): Boolean {
        return socket.isConnected && !socket.isClosed
    }

    fun getServerStatus() {
        val messageBuilder = ClientIncomingMessageBuilder()
            .setType(ClientMessageType.Status)
            .setId(clientID)
            .setTimestamp(Timestamp(System.currentTimeMillis()))

        val message = messageBuilder.build()
        val jsonMessage = jsonClientIncomingMessageAdapter.toJson(message)
        writer.println(jsonMessage)
    }
}