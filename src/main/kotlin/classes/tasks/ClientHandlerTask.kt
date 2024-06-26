package main.classes.tasks

import main.data_classes.KKOQueueMessage
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
        println("[Client Handler] Client Handler task stopped")
        //TODO: Implement client removal from topics list
    }
}