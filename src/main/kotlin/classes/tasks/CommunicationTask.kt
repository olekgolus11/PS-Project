package main.classes.tasks

import main.interfaces.ServerTask
import main.util.ServerConfig
import java.net.InetAddress
import java.net.ServerSocket
import java.net.SocketException
import java.net.SocketTimeoutException

class CommunicationTask(private val listeningAddress: InetAddress) : ServerTask {
    private var stopServer = false
    private var clientHandlerThreads: MutableList<Thread> = mutableListOf()

    override fun run() {
        println("Communication task is running")

        ServerSocket(ServerConfig.listenPort, 0, listeningAddress).use { serverSocket ->
            serverSocket.soTimeout = ServerConfig.timeOut * 1000

            while (!stopServer) {
                try {
                    println("Waiting for client connection")
                    val clientSocket = serverSocket.accept()
                    val clientHandlerThread = Thread(ClientHandlerTask(clientSocket))
                    clientHandlerThread.start()
                    clientHandlerThreads.add(clientHandlerThread)
                } catch (e: SocketTimeoutException) {
                    if (!stopServer) {
                        println("Socket timeout")
                    }
                } catch (e: SocketException) {
                    if (!stopServer) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun stop() {
        stopServer = true
        clientHandlerThreads.forEach { it.join() }
    }
}