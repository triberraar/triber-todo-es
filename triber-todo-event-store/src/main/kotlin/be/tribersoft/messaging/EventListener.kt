package be.tribersoft.messaging

import be.tribersoft.service.EventService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
open class EventListener(val eventService: EventService) {

    private val LOGGER: Logger = LoggerFactory.getLogger(this.javaClass)

    @StreamListener(EventChannel.EVENT_CHANNEL)
    fun on(event: String) {
        LOGGER.info("Received an event $event")
        eventService.save(event)
    }
}