package main.classes.tasks

import main.interfaces.ServerTask
import main.util.MessageQueues

class ResolverTask : ServerTask {
    private var stopResolving = false

    override fun run() {
        println("[Resolver] Resolver task is running")

        while (!stopResolving) {
            if (MessageQueues.KKW.isEmpty()) {
                Thread.sleep(1)
                continue
            }

            val kkwQueueMessage = MessageQueues.KKW.poll()
            kkwQueueMessage.clientOutgoingMessage.type.exectue(kkwQueueMessage)

            println("[Resolver] Resolved message: ${kkwQueueMessage.clientOutgoingMessage.topic}")
        }
    }

    override fun stop() {
        stopResolving = true
        println("[Resolver] Resolver task is stopping")
    }
}