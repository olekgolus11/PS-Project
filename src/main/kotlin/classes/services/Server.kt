package main.classes.services
import main.adapters.JsonConfigAdapter
import main.classes.tasks.CommunicationTask
import main.classes.tasks.MonitoringTask
import main.classes.tasks.UserInterfaceTask
import main.data_classes.Config

class Server(configFileName: String) {
    private var config: Config;
    private val jsonConfigAdapter = JsonConfigAdapter()
    private val serverTaskThreads: MutableList<Thread> = mutableListOf()

    init {
        javaClass.getResourceAsStream(configFileName).use { inputStream ->
            config = jsonConfigAdapter.fromJson(inputStream)
        }

        setupServerTaskThreads()
        runServerTaskThreads()
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