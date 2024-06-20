package main.classes.tasks

import main.interfaces.ServerTask

class MonitoringTask : ServerTask {
    override fun run() {
        println("Monitoring task is running")
    }
}