package main.classes.tasks

import main.interfaces.ServerTask
import main.util.ServerConfig
import java.net.InetAddress
import java.net.ServerSocket
import java.net.SocketException
import java.net.SocketTimeoutException

class CommunicationTask(private val listeningAddress: InetAddress) : ServerTask {
    private var isRunning = true
    private var clientHandlerTasks: MutableList<ClientHandlerTask> = mutableListOf()
    private var clientHandlerThreads: MutableList<Thread> = mutableListOf()

    override fun run() {
        println("[Communication] Communication task is running")

        ServerSocket(ServerConfig.listenPort, 0, listeningAddress).use { serverSocket ->
            serverSocket.soTimeout = ServerConfig.timeOut * 1000

            while (isRunning) {
                try {
                    val clientSocket = serverSocket.accept()
                    val clientHandlerTask = ClientHandlerTask(clientSocket)
                    val clientHandlerThread = Thread(clientHandlerTask)
                    clientHandlerThread.start()
                    clientHandlerTasks.add(clientHandlerTask)
                    clientHandlerThreads.add(clientHandlerThread)
                } catch (_: SocketTimeoutException) {
                } catch (e: SocketException) {
                    if (isRunning) {
                        println("[Communication] Socket exception: ${e.message}")
                    }
                }
            }
        }
    }

    override fun stop() {
        println("[Communication] Communication task is stopping")
        isRunning = false
        clientHandlerTasks.forEach { it.stop() }
        clientHandlerThreads.forEach { it.join() }
        println("[Communication] Communication task stopped")
    }
}