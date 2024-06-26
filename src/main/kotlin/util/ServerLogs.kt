package main.util

import main.data_classes.KKWQueueMessage

object ServerLogs {
    private val logs: MutableList<KKWQueueMessage> = mutableListOf()

    fun addLog(log: KKWQueueMessage) {
        logs.add(log)
    }

    fun getLogs(): List<KKWQueueMessage> {
        return logs
    }
}