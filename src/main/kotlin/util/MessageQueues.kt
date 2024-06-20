package main.util

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ConcurrentMap

object MessageQueues {
    val KKO: ConcurrentLinkedQueue<String> = ConcurrentLinkedQueue()
    val KKW: ConcurrentLinkedQueue<String> = ConcurrentLinkedQueue()
    val LT: ConcurrentMap<String, Int> = ConcurrentHashMap()
}