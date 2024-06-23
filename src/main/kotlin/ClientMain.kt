package main

import main.classes.services.Client
import main.classes.services.Server

fun main() {
    val client = Client()
    client.start("127.0.0.1", 1234, "client1")
}