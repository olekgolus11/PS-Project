package main.classes.sealed_classes

sealed class QueueMessageType {
    object Register : QueueMessageType()
    object Withdraw : QueueMessageType()
    object Reject : QueueMessageType()
    object Acknowledge : QueueMessageType()
    object Message : QueueMessageType()
    object Status : QueueMessageType()
}