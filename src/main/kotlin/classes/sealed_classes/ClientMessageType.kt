package main.classes.sealed_classes

import com.squareup.moshi.Json
import main.data_classes.ClientMessage

sealed class ClientMessageType {
    abstract fun exectue(queueMessage: ClientMessage)
    @Json(name = "register")
    data object Register : ClientMessageType() {
        override fun exectue(queueMessage: ClientMessage) {
            println("Registering ${queueMessage.id}")
        }
    }

    @Json(name = "withdraw")
    data object Withdraw : ClientMessageType() {
        override fun exectue(queueMessage: ClientMessage) {
            println("Withdrawing ${queueMessage.id}")
        }
    }

    @Json(name = "reject")
    data object Reject : ClientMessageType() {
        override fun exectue(queueMessage: ClientMessage) {
            println("Rejecting ${queueMessage.id}")
        }
    }

    @Json(name = "acknowledge")
    data object Acknowledge : ClientMessageType() {
        override fun exectue(queueMessage: ClientMessage) {
            println("Acknowledging ${queueMessage.id}")
        }
    }

    @Json(name = "message")
    data object Message : ClientMessageType() {
        override fun exectue(queueMessage: ClientMessage) {
            println("Sending message to ${queueMessage.id}")
        }
    }

    @Json(name = "status")
    data object Status : ClientMessageType() {
        override fun exectue(queueMessage: ClientMessage) {
            println("Checking status of ${queueMessage.id}")
        }
    }
}