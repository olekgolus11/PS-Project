package main.classes.tasks

import main.interfaces.ServerTask

class CommunicationTask : ServerTask {
    override fun run() {
        println("Communication task is running")
    }
}