package main.classes.services
import main.adapters.JsonConfigAdapter
import main.classes.tasks.CommunicationTask
import main.classes.tasks.MonitoringTask
import main.classes.tasks.UserInterfaceTask
import main.util.ServerConfig

class Server(configFileName: String) {
    private val jsonConfigAdapter = JsonConfigAdapter()
    private val serverTaskThreads: MutableList<Thread> = mutableListOf()

    init {
        loadConfigFromFile(configFileName)
        setupServerTaskThreads()
        runServerTaskThreads()
    }

    private fun loadConfigFromFile(configFileName: String) {
        ServerConfig.loadFromJson(jsonConfigAdapter, configFileName)
    }

    private fun setupServerTaskThreads() {
        val communicationThread = Thread(CommunicationTask())
        val monitoringThread = Thread(MonitoringTask())
        val userInterfaceThread = Thread(UserInterfaceTask())

        serverTaskThreads.add(communicationThread)
        serverTaskThreads.add(monitoringThread)
        serverTaskThreads.add(userInterfaceThread)
    }

    private fun runServerTaskThreads() {
        serverTaskThreads.forEach { it.start() }
    }

}