package main.classes.tasks

import main.classes.builders.ClientIncomingMessageBuilder
import main.data_classes.ClientIncomingMessage
import main.interfaces.ServerTask
import main.util.MessageQueues
import main.util.ServerConfig

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
            val clientMessage = kkwQueueMessage.clientOutgoingMessage
            val clientReceiverRefs = kkwQueueMessage.clientRefs

            val messageToResend = ClientIncomingMessageBuilder()
                .setId(ServerConfig.serverId)
                .setType(clientMessage.type)
                .setTimestamp(clientMessage.timestamp)
                .setTopic(clientMessage.topic)
                .setPayload(clientMessage.payload)
                .build()

            clientReceiverRefs.forEach {
                messageToResend.type.exectue(messageToResend, it)
            }

            println("[Resolver] Resolved message: ${kkwQueueMessage.clientOutgoingMessage.topic}")
        }
    }

    override fun stop() {
        stopResolving = true
        println("[Resolver] Resolver task is stopping")
    }
}