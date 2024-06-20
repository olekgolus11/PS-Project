package main.classes
import main.adapters.JsonConfigAdapter
import main.data_classes.Config
import java.io.InputStream

public class Server(configFileName: String) {
    private var config: Config;
    private val jsonConfigAdapter = JsonConfigAdapter()

    init {
        javaClass.getResourceAsStream(configFileName).use { inputStream ->
            config = jsonConfigAdapter.fromJson(inputStream)
        }
        println("Server started with config: $config")
    }

}