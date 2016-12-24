package be.tribersoft.messaging

import be.tribersoft.domain.Aggregate
import be.tribersoft.domain.AggregateRepository
import be.tribersoft.domain.EventRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.awaitility.Awaitility.await
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.GenericMessage
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class EventListenerOnEventTest() {

    @Autowired
    @Qualifier("event")
    private lateinit var eventChannel: MessageChannel

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var eventRepository: EventRepository

    @Autowired
    private lateinit var aggregateRepository: AggregateRepository

    @After
    fun cleanUp() {
        eventRepository.deleteAll()
        aggregateRepository.deleteAll()
    }

    @Test
    fun eventIsSavedToEventStore() {
        val inputJson = ExampleJson("type", UUID.randomUUID(), 0L, LocalDateTime.now(), "some property")
        val message = GenericMessage(objectMapper.writeValueAsString(inputJson))
        eventChannel.send(message);

        await().atMost(5, TimeUnit.SECONDS).until(Runnable {
            val events = eventRepository.findAll()
            assertThat(events).hasSize(1)
            assertThat(events).extracting("aggregateUuid", "order", "timestamp").containsOnly(tuple(inputJson.aggregateUuid.toString(), inputJson.order, inputJson.timestamp))
            assertThat(events[0].id).isNotNull()
            val readValue = objectMapper.readValue(events[0].content, ExampleJson::class.java)
            assertThat(readValue).isEqualTo(inputJson)
            val aggregates = aggregateRepository.findAll()
            assertThat(aggregates).hasSize(1)
            assertThat(aggregates[0]).isEqualTo(Aggregate(inputJson.aggregateUuid.toString()))
        })
    }

    @Test
    fun only1AggregateIsSavedForMultipleEventsForSameAggregate() {
        val inputJson = ExampleJson("type", UUID.randomUUID(), 0L, LocalDateTime.now(), "some property")
        val message = GenericMessage(objectMapper.writeValueAsString(inputJson))
        eventChannel.send(message);

        val inputJson2 = ExampleJson("type", inputJson.aggregateUuid, 0L, LocalDateTime.now(), "other property")
        val message2 = GenericMessage(objectMapper.writeValueAsString(inputJson2))
        eventChannel.send(message2);

        await().atMost(5, TimeUnit.SECONDS).until(Runnable {
            val events = eventRepository.findAll()
            assertThat(events).hasSize(2)
            assertThat(events).extracting("aggregateUuid", "order", "timestamp").containsOnly(tuple(inputJson.aggregateUuid.toString(), inputJson.order, inputJson.timestamp), tuple(inputJson2.aggregateUuid.toString(), inputJson2.order, inputJson2.timestamp))
            assertThat(events[0].id).isNotNull()
            val readValue = objectMapper.readValue(events[0].content, ExampleJson::class.java)
            assertThat(readValue).isEqualTo(inputJson)

            assertThat(events[1].id).isNotNull()
            val readValue2 = objectMapper.readValue(events[1].content, ExampleJson::class.java)
            assertThat(readValue2).isEqualTo(inputJson2)


            val aggregates = aggregateRepository.findAll()
            assertThat(aggregates).hasSize(1)
            assertThat(aggregates[0]).isEqualTo(Aggregate(inputJson.aggregateUuid.toString()))
        })
    }
}

data class ExampleJson(val type: String, val aggregateUuid: UUID, val order: Long, val timestamp: LocalDateTime, val property: String)
