package main.classes.tasks

import java.net.Socket

class ClientHandlerTask(private val clientSocket: Socket) : Runnable {
    override fun run() {
        println("Client handler task is running")
    }
}