package main.classes.services
import main.adapters.JsonConfigAdapter
import main.classes.tasks.CommunicationTask
import main.classes.tasks.MonitoringTask
import main.classes.tasks.ResolverTask
import main.classes.tasks.UserInterfaceTask
import main.data_classes.Config
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
        val monitoringThread = Thread(MonitoringTask())
        serverTaskThreads.add(monitoringThread)

        val userInterfaceThread = Thread(UserInterfaceTask())
        serverTaskThreads.add(userInterfaceThread)

        val resolverThread = Thread(ResolverTask())
        serverTaskThreads.add(resolverThread)

        ServerConfig.listenAddresses.forEach { listeningAddress ->
            val communicationThread = Thread(CommunicationTask(listeningAddress))
            serverTaskThreads.add(communicationThread)
        }
    }

    private fun runServerTaskThreads() {
        serverTaskThreads.forEach { it.start() }
    }

}