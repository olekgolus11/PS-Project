package main.util

import main.data_classes.KKOQueueMessage
import main.data_classes.KKWQueueMessage
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ConcurrentMap

object MessageQueues {
    val KKO: ConcurrentLinkedQueue<KKOQueueMessage> = ConcurrentLinkedQueue()
    val KKW: ConcurrentLinkedQueue<KKWQueueMessage> = ConcurrentLinkedQueue()
    val LT: ConcurrentMap<String, Int> = ConcurrentHashMap()
}