package main.data_classes

data class Config(
    val serverId: String,
    val listenAddresses: List<String>,
    val listenPort: Int,
    val timeOut: Int
)
