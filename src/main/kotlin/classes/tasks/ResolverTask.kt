package main.classes.tasks

import main.classes.builders.ClientIncomingMessageBuilder
import main.interfaces.ServerTask
import main.util.MessageQueues
import main.util.ServerConfig
import main.util.ServerLogs

class ResolverTask : ServerTask {
    private var isRunning = true

    override fun run() {
        println("[Resolver] Resolver task is running")

        while (isRunning) {
            if (MessageQueues.KKW.isEmpty()) {
                Thread.sleep(100)
                continue
            }

            val kkwQueueMessage = MessageQueues.KKW.poll()
            val clientMessage = kkwQueueMessage.clientOutgoingMessage
            val clientReceiverRefs = kkwQueueMessage.clientRefs

            if (clientMessage.payload?.get("logs") == null) {
                ServerLogs.addLog(kkwQueueMessage)
            }

            val messageToResend = ClientIncomingMessageBuilder()
                .copy(clientMessage)
                .setId(ServerConfig.serverId)
                .build()

            println("[Resolver] Resolving message: $messageToResend")

            clientReceiverRefs.forEach {
                try {
                    messageToResend.type.execute(messageToResend, it)
                } catch (e: Exception) {
                    println("[Resolver] Socket already closed")
                }
            }

            println("[Resolver] Resolved message: ${kkwQueueMessage.clientOutgoingMessage.topic}")
        }
    }

    override fun stop() {
        isRunning = false
        println("[Resolver] Resolver task is stopping")
    }
}