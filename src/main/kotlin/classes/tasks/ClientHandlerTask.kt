package main.classes.tasks

import main.interfaces.ServerTask
import java.net.Socket

class ClientHandlerTask(private val clientSocket: Socket) : ServerTask {
    override fun run() {
        println("Client handler task is running")
    }

    override fun stop() {
        println("Client handler task is stopping")
    }
}