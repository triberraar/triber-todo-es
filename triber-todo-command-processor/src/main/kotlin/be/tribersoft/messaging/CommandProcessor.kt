package be.tribersoft.messaging

import akka.actor.ActorRef
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cloud.stream.annotation.Input
import org.springframework.cloud.stream.annotation.Output
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.integration.annotation.Gateway
import org.springframework.integration.annotation.MessagingGateway
import org.springframework.messaging.MessageChannel
import org.springframework.stereotype.Component


interface CommandChannel {

    @Input(INPUT_CHANNEL_NAME)
    fun commandInput(): MessageChannel

    companion object {
        const val INPUT_CHANNEL_NAME = "command"
    }
}

interface EventChannel {
    @Output(EventChannel.OUTPUT_CHANNEL_NAME)
    fun commandOutput(): MessageChannel

    companion object {
        const val OUTPUT_CHANNEL_NAME = "event"
    }
}

@MessagingGateway
interface EventGateway {
    @Gateway(requestChannel = EventChannel.OUTPUT_CHANNEL_NAME)
    fun emit(event: Event)
}

@Component
open class CommandProcessor(@Qualifier("supervisorActor") val supervisorActor: ActorRef) {

    private val LOGGER: Logger = LoggerFactory.getLogger(this.javaClass)

    @StreamListener(CommandChannel.INPUT_CHANNEL_NAME)
    fun on(command: Command) {
        LOGGER.info("Received a command: $command")
        supervisorActor.tell(command, null)
    }
}