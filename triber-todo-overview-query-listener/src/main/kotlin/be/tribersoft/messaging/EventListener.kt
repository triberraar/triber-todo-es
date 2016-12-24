package be.tribersoft.messaging

import akka.actor.ActorRef
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.Input
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.messaging.MessageChannel

interface EventChannel {
    @Input(EventChannel.EVENT_CHANNEL)
    fun commandOutput(): MessageChannel

    companion object {
        const val EVENT_CHANNEL = "event"
    }
}

@EnableBinding(EventChannel::class)
open class EventListener(@Qualifier("supervisorActor") val supervisorActor: ActorRef) {

    private val LOGGER: Logger = LoggerFactory.getLogger(this.javaClass)

    @StreamListener(EventChannel.EVENT_CHANNEL)
    fun on(event: TodoListCreatedEvent) {
        LOGGER.info("Received a todo list created event $event")
        supervisorActor.tell(event, null)
    }
}