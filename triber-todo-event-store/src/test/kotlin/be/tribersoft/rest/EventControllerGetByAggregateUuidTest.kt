package be.tribersoft.rest

import be.tribersoft.domain.Event
import be.tribersoft.domain.EventRepository
import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class EventControllerGetByAggregateUuidTest() {

    @Value("\${local.server.port}")
    private lateinit var serverPort: Integer

    @Autowired
    private lateinit var eventRepository: EventRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @After
    fun cleanUp() {
        eventRepository.deleteAll()
    }


    private val aggregateUUID = UUID.randomUUID()
    private lateinit var event1ForAggregate: Event
    private lateinit var event2ForAggregate: Event
    private lateinit var event3ForAggregate: Event
    private lateinit var eventForOtherAggregate: Event

    @Before
    fun setUp() {
        RestAssured.port = serverPort.toInt();
        val exampleJsonForEvent1ForAggregate = ExampleJson("type1", aggregateUUID.toString(), 0L, LocalDateTime.now(), "some property1")
        val exampleJsonForEvent2ForAggregate = ExampleJson("type2", aggregateUUID.toString(), 1L, LocalDateTime.now(), "some property2")
        val exampleJsonForEvent3ForAggregate = ExampleJson("type3", aggregateUUID.toString(), 2L, LocalDateTime.now(), "some property3")
        val exampleJsonForEventForOtherAggregate = ExampleJson("type", UUID.randomUUID().toString(), 0L, LocalDateTime.now(), "some property")
        event1ForAggregate = Event(exampleJsonForEvent1ForAggregate.type, exampleJsonForEvent1ForAggregate.aggregateUuid, exampleJsonForEvent1ForAggregate.order, exampleJsonForEvent1ForAggregate.timestamp, objectMapper.writeValueAsString(exampleJsonForEvent1ForAggregate))
        event2ForAggregate = Event(exampleJsonForEvent2ForAggregate.type, exampleJsonForEvent2ForAggregate.aggregateUuid, exampleJsonForEvent2ForAggregate.order, exampleJsonForEvent2ForAggregate.timestamp, objectMapper.writeValueAsString(exampleJsonForEvent2ForAggregate))
        event3ForAggregate = Event(exampleJsonForEvent3ForAggregate.type, exampleJsonForEvent3ForAggregate.aggregateUuid, exampleJsonForEvent3ForAggregate.order, exampleJsonForEvent3ForAggregate.timestamp, objectMapper.writeValueAsString(exampleJsonForEvent3ForAggregate))
        eventForOtherAggregate = Event(exampleJsonForEventForOtherAggregate.type, exampleJsonForEventForOtherAggregate.aggregateUuid, exampleJsonForEventForOtherAggregate.order, exampleJsonForEventForOtherAggregate.timestamp, objectMapper.writeValueAsString(exampleJsonForEventForOtherAggregate))

        eventRepository.save(event1ForAggregate)
        eventRepository.save(event3ForAggregate)
        eventRepository.save(event2ForAggregate)
        eventRepository.save(eventForOtherAggregate)
    }

    @Test
    fun returnsAllEventsSortedOnOrderForTheAggregate() {
        given()
                .pathParam("aggregateUuid", aggregateUUID.toString())
                .accept(ContentType.JSON)
                .`when`()
                .get("/event/{aggregateUuid}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", `is`(3))
                .body("[0].size()", `is`(5))
                .body("[0].type", `is`(event1ForAggregate.type))
                .body("[0].aggregateUuid", `is`(event1ForAggregate.aggregateUuid))
                .body("[0].order", `is`(0))
                .body("[0].timestamp", `is`(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").format(event1ForAggregate.timestamp).trim('0')))
                .body("[0].property", `is`("some property1"))
                .body("[1].size()", `is`(5))
                .body("[1].type", `is`(event2ForAggregate.type))
                .body("[1].aggregateUuid", `is`(event2ForAggregate.aggregateUuid))
                .body("[1].order", `is`(1))
                .body("[1].timestamp", `is`(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").format(event2ForAggregate.timestamp).trim('0')))
                .body("[1].property", `is`("some property2"))
                .body("[2].size()", `is`(5))
                .body("[2].type", `is`(event3ForAggregate.type))
                .body("[2].aggregateUuid", `is`(event3ForAggregate.aggregateUuid))
                .body("[2].order", `is`(2))
                .body("[2].timestamp", `is`(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").format(event3ForAggregate.timestamp).trim('0')))
                .body("[2].property", `is`("some property3"))
    }
}

data class ExampleJson(val type: String, val aggregateUuid: String, val order: Long, val timestamp: LocalDateTime, val property: String)