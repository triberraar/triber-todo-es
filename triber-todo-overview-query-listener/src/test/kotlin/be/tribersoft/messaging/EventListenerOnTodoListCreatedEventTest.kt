package be.tribersoft.messaging

import be.tribersoft.domain.TodoList
import be.tribersoft.domain.TodoListElasticRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate
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
class EventListenerOnTodoListCreatedEventTest() {

    @Autowired
    @Qualifier("event")
    private lateinit var eventChannel: MessageChannel

    @Autowired
    private lateinit var objectmapper: ObjectMapper

    @Autowired
    private lateinit var repository: TodoListElasticRepository

    @Autowired
    private lateinit var elasticsearchTemplate: ElasticsearchTemplate

    @Before
    fun setUp() {
        elasticsearchTemplate.deleteIndex(TodoList::class.java)
        elasticsearchTemplate.createIndex(TodoList::class.java)
        elasticsearchTemplate.refresh(TodoList::class.java)
    }

    @Test
    fun createsANewTodoList() {
        val uuid = UUID.randomUUID()
        val now = LocalDateTime.now()
        val event = TodoListCreatedEvent("name", uuid, 0L, now)
        val eventMessage = GenericMessage(objectmapper.writeValueAsString(event))

        eventChannel.send(eventMessage)

        await().atMost(5, TimeUnit.SECONDS).until(Runnable {
            val foundTodoList = repository.findOne(uuid.toString())
            assertThat(foundTodoList).isNotNull()
            assertThat(foundTodoList).isEqualToComparingFieldByField(TodoList(uuid.toString(), "name", now))
        })
    }
}