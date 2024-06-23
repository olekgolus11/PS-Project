package main.classes.tasks

import main.interfaces.ServerTask

class UserInterfaceTask : ServerTask {
    override fun run() {
        println("[User Interface] User Interface task is running")
    }

    override fun stop() {
        println("[User Interface] User Interface task is stopping")
    }
}