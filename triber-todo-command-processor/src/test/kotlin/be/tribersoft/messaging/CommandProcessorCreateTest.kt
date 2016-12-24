package be.tribersoft.messaging

import be.tribersoft.client.EventStoreClientImpl
import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.path.json.JsonPath
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.test.binder.MessageCollector
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.GenericMessage
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.util.*
import java.util.concurrent.TimeUnit


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CommandProcessorCreateTest() {
    @Autowired
    @Qualifier("command")
    private lateinit var commandChannel: MessageChannel

    @Autowired
    @Qualifier("event")
    private lateinit var eventChannel: MessageChannel

    @Autowired
    private lateinit var objectmapper: ObjectMapper

    @Autowired
    private lateinit var messageCollector: MessageCollector

    @Autowired
    private lateinit var eventStoreClient: EventStoreClientImpl

    @Test
    fun createTodoListCommandIsTransformedToCreateTodoListEventThroughActor() {
        val command = CreateTodoListCommand("name", UUID.randomUUID())
        val commandMessage = GenericMessage(objectmapper.writeValueAsString(command))

        commandChannel.send(commandMessage);
        await().atMost(5, TimeUnit.SECONDS).until(java.lang.Runnable {
            val message = messageCollector.forChannel(eventChannel).poll()
            assertThat(message).isNotNull();
            val event = JsonPath.from(message.payload as String).getObject("", TodoListCreatedEvent::class.java)
            assertThat(event.name).isEqualTo("name")
            assertThat(event.getAggregateUuid()).isEqualTo(command.getAggregateUuid())
            assertThat(event.getTimestamp()).isNotNull()
            assertThat(event.getOrder()).isEqualTo(0)
        })
        verify(eventStoreClient).getEvents(command.getAggregateUuid().toString());
    }
}