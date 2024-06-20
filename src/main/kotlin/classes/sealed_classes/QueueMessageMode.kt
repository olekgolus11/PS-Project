package main.classes.sealed_classes

sealed class QueueMessageMode {
    object Producer : QueueMessageMode()
    object Subscriber : QueueMessageMode()
}