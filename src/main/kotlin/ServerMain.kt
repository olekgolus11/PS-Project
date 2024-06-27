package main

import main.classes.services.Server

fun main() {
    try {
        val server = Server("/config.json")
    } catch (e: Exception) {
        println("Server error: ${e.message}")
    }
}