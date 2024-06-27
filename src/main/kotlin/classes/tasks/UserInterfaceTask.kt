package main.classes.tasks

import main.classes.services.Server
import main.interfaces.ServerTask
import main.util.MessageQueues
import main.util.ServerConfig
import java.io.BufferedReader
import java.io.InputStreamReader

class UserInterfaceTask(private val server: Server) : ServerTask {
    private var isRunning = true

    override fun run() {
        println("[User Interface] User Interface task is running")

        val consoleReader = BufferedReader(InputStreamReader(System.`in`))
        while (isRunning) {
            val command = consoleReader.readLine()

            when (command) {
                "stopServer" -> {
                    server.stop()
                    println("[User Interface] Server and all client tasks have been stopped")
                    break
                }
                "getServerInfo" -> {
                    val port = ServerConfig.listenPort
                    val networkInterfaces = ServerConfig.listenAddresses
                    println("[User Interface] Server is listening on port $port and network interfaces $networkInterfaces")
                }
                "getTopicsInfo" -> {
                    val topicsInfo = MessageQueues.LT
                    println("[User Interface] Topics information:")
                    topicsInfo.forEach { (_, topicInfo) ->
                        println("Topic: $topicInfo")
                    }
                }
                else -> println("[User Interface] Unknown command: $command")
            }
        }
    }

    override fun stop() {
        isRunning = false
        println("[User Interface] User Interface task is stopping")
    }
}