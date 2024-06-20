package main.classes.tasks

import main.interfaces.ServerTask

class UserInterfaceTask : ServerTask {
    override fun run() {
        println("User Interface task is running")
    }
}