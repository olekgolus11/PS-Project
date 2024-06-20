package main.util

import main.adapters.JsonConfigAdapter
import java.net.InetAddress

object ServerConfig {
    var serverId: String = ""
    var listenAddresses: List<InetAddress> = listOf()
    var listenPort: Int = 0
    var timeOut: Int = 0

    fun loadFromJson(jsonConfigAdapter: JsonConfigAdapter, configFileName: String) {
        javaClass.getResourceAsStream(configFileName).use { inputStream ->
            val config = jsonConfigAdapter.fromJson(inputStream)
            serverId = config.serverId
            listenAddresses = config.listenAddresses.map {
                if (it == "*") InetAddress.getByName("0.0.0.0") else InetAddress.getByName(it)
            }
            listenPort = config.listenPort
            timeOut = config.timeOut
        }
    }
}