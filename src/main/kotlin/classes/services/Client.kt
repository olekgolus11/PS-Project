package main.classes.services

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class Client {
    private lateinit var clientID: String
    private lateinit var reader: BufferedReader
    private lateinit var writer: PrintWriter

    fun start(serverIP: String, serverPort: Int, clientID: String) {
        this.clientID = clientID

        val socket = Socket(serverIP, serverPort)
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

        println("Client $clientID is connected to server at $serverIP:$serverPort")
    }
}