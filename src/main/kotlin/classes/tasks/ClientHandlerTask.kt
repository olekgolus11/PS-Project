package main.classes.tasks

import com.squareup.moshi.JsonDataException
import main.adapters.JsonQueueMessageAdapter
import main.interfaces.ServerTask
import main.util.MessageQueues
import java.net.Socket
import main.util.ServerConfig
import java.net.SocketException
import java.net.SocketTimeoutException
import java.io.BufferedReader
import java.io.InputStreamReader

class ClientHandlerTask(private val clientSocket: Socket) : ServerTask {
    private var stopClient = false
    private val jsonQueueMessageAdapter = JsonQueueMessageAdapter()

    override fun run() {
        println("[Client Handler] task is running")

        clientSocket.soTimeout = ServerConfig.timeOut * 1000
        val reader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))

        try {
            while (!stopClient) {
                val message = reader.readLine()

                try {
                    val queueMessage = jsonQueueMessageAdapter.fromJson(message)
                    MessageQueues.KKO.add(queueMessage)
                } catch (e: JsonDataException) {
                    println("[Client Handler] Failed to deserialize JSON: ${e.message}")
                }
            }
        } catch (e: SocketTimeoutException) {
            println("[Client Handler] Socket timeout")
        } catch (e: SocketException) {
            if (!stopClient) {
                e.printStackTrace()
            }
        }
    }

    override fun stop() {
        stopClient = true
        clientSocket.close()
        println("[Client Handler] task is stopping")
    }
}