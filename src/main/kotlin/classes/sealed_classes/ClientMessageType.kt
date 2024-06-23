package main.classes.sealed_classes

import com.squareup.moshi.Json
import main.data_classes.ClientIncomingMessage

sealed class ClientMessageType {
    abstract fun exectue(queueMessage: ClientIncomingMessage)
    @Json(name = "register")
    data object Register : ClientMessageType() {
        override fun exectue(queueMessage: ClientIncomingMessage) {
            println("Registering ${queueMessage.id}")
        }
    }

    @Json(name = "withdraw")
    data object Withdraw : ClientMessageType() {
        override fun exectue(queueMessage: ClientIncomingMessage) {
            println("Withdrawing ${queueMessage.id}")
        }
    }

    @Json(name = "reject")
    data object Reject : ClientMessageType() {
        override fun exectue(queueMessage: ClientIncomingMessage) {
            println("Rejecting ${queueMessage.id}")
        }
    }

    @Json(name = "acknowledge")
    data object Acknowledge : ClientMessageType() {
        override fun exectue(queueMessage: ClientIncomingMessage) {
            println("Acknowledging ${queueMessage.id}")
        }
    }

    @Json(name = "message")
    data object Message : ClientMessageType() {
        override fun exectue(queueMessage: ClientIncomingMessage) {
            println("Sending message to ${queueMessage.id}")
        }
    }

    @Json(name = "status")
    data object Status : ClientMessageType() {
        override fun exectue(queueMessage: ClientIncomingMessage) {
            println("Checking status of ${queueMessage.id}")
        }
    }
}