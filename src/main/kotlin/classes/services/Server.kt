package main.classes.services
import main.adapters.JsonConfigAdapter
import main.classes.tasks.CommunicationTask
import main.classes.tasks.MonitoringTask
import main.classes.tasks.ResolverTask
import main.classes.tasks.UserInterfaceTask
import main.data_classes.Config
import main.interfaces.ServerTask
import main.util.MessageQueues
import main.util.ServerConfig
import java.net.InetAddress

class Server(configFileName: String) {
    private val jsonConfigAdapter = JsonConfigAdapter()
    private val serverTaskThreads: MutableList<Thread> = mutableListOf()
    private val serverTasks: MutableList<ServerTask> = mutableListOf()

    init {
        loadConfigFromFile(configFileName)
        setupServerTaskThreads()
        runServerTaskThreads()
    }

    private fun loadConfigFromFile(configFileName: String) {
        ServerConfig.loadFromJson(jsonConfigAdapter, configFileName)
    }

    private fun setupServerTaskThreads() {
        val monitoringTask = MonitoringTask()
        val monitoringThread = Thread(monitoringTask)
        serverTaskThreads.add(monitoringThread)
        serverTasks.add(monitoringTask)

        val userInterfaceTask = UserInterfaceTask(this)
        val userInterfaceThread = Thread(userInterfaceTask)
        serverTaskThreads.add(userInterfaceThread)
        serverTasks.add(userInterfaceTask)

        val resolverTask = ResolverTask()
        val resolverThread = Thread(resolverTask)
        serverTaskThreads.add(resolverThread)
        serverTasks.add(resolverTask)

        ServerConfig.listenAddresses.forEach { listeningAddress ->
            val communicationTask = CommunicationTask(listeningAddress)
            val communicationThread = Thread(communicationTask)
            serverTaskThreads.add(communicationThread)
            serverTasks.add(communicationTask)
        }
    }

    private fun runServerTaskThreads() {
        serverTaskThreads.forEach { it.start() }
    }

    fun stop() {
        serverTasks.forEach { it.stop() }
    }
}