package be.tribersoft.service

import be.tribersoft.domain.Aggregate
import be.tribersoft.domain.AggregateRepository
import be.tribersoft.domain.Event
import be.tribersoft.domain.EventRepository
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*


@Component
class EventService(val eventRepository: EventRepository, val aggregateRepository: AggregateRepository, val eventTransformer: EventTransformer) {

    private val LOGGER: Logger = LoggerFactory.getLogger(this.javaClass)

    fun save(event: String) {
        val event = eventTransformer.toEventJson(event)
        eventRepository.save(event)
        if (!aggregateRepository.exists(event.aggregateUuid)) {
            val aggregate = aggregateRepository.save(Aggregate(event.aggregateUuid))
            LOGGER.info("Saved an aggregate to the eventStore: $aggregate")
        }
        LOGGER.info("saved an event to the eventStore: $event")
    }

    fun findByAggregateUuid(aggregateUuid: UUID): List<Map<String, Any>> {
        return eventRepository.findByAggregateUuidOrderByOrderAsc(aggregateUuid.toString())
                .map { eventTransformer.fromEvent(it) }

    }

}

@Component
class EventTransformer(val objectMapper: ObjectMapper) {

    fun toEventJson(event: String): Event {
        val eventJson = objectMapper.readValue(event, EventJson::class.java)
        return Event(eventJson.type, eventJson.aggregateUuid.toString(), eventJson.order, eventJson.timestamp, event)
    }

    fun fromEvent(event: Event): Map<String, Any> {

        return objectMapper.readValue<Map<String, Any>>(event.content, object : TypeReference<Map<String, Any>>() {
        })
    }
}

data class EventJson(val type: String, val aggregateUuid: UUID, val order: Long, val timestamp: LocalDateTime)