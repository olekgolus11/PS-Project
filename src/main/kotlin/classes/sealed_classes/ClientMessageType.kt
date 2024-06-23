package main.classes.sealed_classes

import com.squareup.moshi.Json
import main.data_classes.KKWQueueMessage

sealed class ClientMessageType {
    abstract fun exectue(queueMessage: KKWQueueMessage)
    @Json(name = "register")
    data object Register : ClientMessageType() {
        override fun exectue(queueMessage: KKWQueueMessage) {
            println("Registering ${queueMessage.clientOutgoingMessage.id}")
        }
    }

    @Json(name = "withdraw")
    data object Withdraw : ClientMessageType() {
        override fun exectue(queueMessage: KKWQueueMessage) {
            println("Withdrawing ${queueMessage.clientOutgoingMessage.id}")
        }
    }

    @Json(name = "reject")
    data object Reject : ClientMessageType() {
        override fun exectue(queueMessage: KKWQueueMessage) {
            println("Rejecting ${queueMessage.clientOutgoingMessage.id}")
        }
    }

    @Json(name = "acknowledge")
    data object Acknowledge : ClientMessageType() {
        override fun exectue(queueMessage: KKWQueueMessage) {
            println("Acknowledging ${queueMessage.clientOutgoingMessage.id}")
        }
    }

    @Json(name = "message")
    data object Message : ClientMessageType() {
        override fun exectue(queueMessage: KKWQueueMessage) {
            println("Sending message to ${queueMessage.clientOutgoingMessage.id}")
        }
    }

    @Json(name = "status")
    data object Status : ClientMessageType() {
        override fun exectue(queueMessage: KKWQueueMessage) {
            println("Checking status of ${queueMessage.clientOutgoingMessage.id}")
        }
    }
}