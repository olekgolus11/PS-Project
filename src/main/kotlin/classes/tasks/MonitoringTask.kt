package main.classes.tasks

import main.interfaces.ServerTask

class MonitoringTask : ServerTask {
    override fun run() {
        println("Monitoring task is running")
    }

    override fun stop() {
        println("Monitoring task is stopping")
    }
}