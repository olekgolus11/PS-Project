package main.classes.sealed_classes

import com.squareup.moshi.Json
import main.data_classes.QueueMessage

sealed class QueueMessageType {
    abstract fun exectue(queueMessage: QueueMessage)
    @Json(name = "register")
    data object Register : QueueMessageType() {
        override fun exectue(queueMessage: QueueMessage) {
            println("Registering ${queueMessage.id}")
        }
    }

    @Json(name = "withdraw")
    data object Withdraw : QueueMessageType() {
        override fun exectue(queueMessage: QueueMessage) {
            println("Withdrawing ${queueMessage.id}")
        }
    }

    @Json(name = "reject")
    data object Reject : QueueMessageType() {
        override fun exectue(queueMessage: QueueMessage) {
            println("Rejecting ${queueMessage.id}")
        }
    }

    @Json(name = "acknowledge")
    data object Acknowledge : QueueMessageType() {
        override fun exectue(queueMessage: QueueMessage) {
            println("Acknowledging ${queueMessage.id}")
        }
    }

    @Json(name = "message")
    data object Message : QueueMessageType() {
        override fun exectue(queueMessage: QueueMessage) {
            println("Sending message to ${queueMessage.id}")
        }
    }

    @Json(name = "status")
    data object Status : QueueMessageType() {
        override fun exectue(queueMessage: QueueMessage) {
            println("Checking status of ${queueMessage.id}")
        }
    }
}