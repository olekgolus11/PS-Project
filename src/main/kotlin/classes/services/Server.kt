package main.classes.services
import main.adapters.JsonConfigAdapter
import main.data_classes.Config

class Server(configFileName: String) {
    private var config: Config;
    private val jsonConfigAdapter = JsonConfigAdapter()

    init {
        javaClass.getResourceAsStream(configFileName).use { inputStream ->
            config = jsonConfigAdapter.fromJson(inputStream)
        }
    }

}