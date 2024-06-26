package main

import classes.services.Client

fun main() {
    print("Enter IP address: ")
    val ipAddress = readlnOrNull() ?: "127.0.0.1"

    print("Enter port: ")
    val port = readlnOrNull()?.toIntOrNull() ?: 1234

    print("Enter client ID: ")
    val clientId = readlnOrNull() ?: run {
        println("Client ID cannot be empty")
        return
    }

    val client = Client()
    client.start(ipAddress, port, clientId)
}