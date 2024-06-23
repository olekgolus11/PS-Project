package main.util

import main.data_classes.ClientMessage
import main.data_classes.KKOQueueMessage
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ConcurrentMap

object MessageQueues {
    val KKO: ConcurrentLinkedQueue<KKOQueueMessage> = ConcurrentLinkedQueue()
    val KKW: ConcurrentLinkedQueue<String> = ConcurrentLinkedQueue()
    val LT: ConcurrentMap<String, Int> = ConcurrentHashMap()
}