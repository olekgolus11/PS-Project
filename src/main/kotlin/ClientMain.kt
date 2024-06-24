package main

import classes.services.Client

fun main() {
    val client = Client()
    client.start("127.0.0.1", 1234, "client1")
}