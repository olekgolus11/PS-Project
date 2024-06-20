package main.classes.tasks

import main.util.ServerConfig
import java.net.InetAddress
import java.net.ServerSocket
import java.net.SocketException
import java.net.SocketTimeoutException

class CommunicationTask : Runnable {
    private var stopServer = false

    override fun run() {
        println("Communication task is running")

        ServerSocket(ServerConfig.listenPort, 0, InetAddress.getByName("0.0.0.0")).use { serverSocket ->
            serverSocket.soTimeout = ServerConfig.timeOut * 1000

            while (!stopServer) {
                try {
                    println("Waiting for client connection")
                    val clientSocket = serverSocket.accept()
                    Thread(ClientHandlerTask(clientSocket)).start()
                } catch (e: SocketTimeoutException) {
                    if (!stopServer) {
                        println("Socket timeout")
                    }
                }
                catch (e: SocketException) {
                    if (!stopServer) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun stop() {
        stopServer = true
    }
}