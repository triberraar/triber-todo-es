package be.tribersoft.rest

import be.tribersoft.messaging.CreateTodoListCommand
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.path.json.JsonPath.from
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.test.binder.MessageCollector
import org.springframework.http.HttpStatus
import org.springframework.messaging.MessageChannel
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TodoListControllerCreateTest {

    @Value("\${local.server.port}")
    private lateinit var serverPort: Integer

    @Autowired
    private lateinit var messageCollector: MessageCollector

    @Autowired
    @Qualifier("command")
    private lateinit var commandChannel: MessageChannel

    @Before
    fun setUp() {
        RestAssured.port = serverPort.toInt();
    }

    @Test
    fun publishesCreateMessage() {
        given()
                .body(CreateTodoListJson("name"))
                .contentType(ContentType.JSON)
                .`when`()
                .post("/todo-list")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("uuid", notNullValue())

        val message = messageCollector.forChannel(commandChannel).poll()
        val command = from(message.payload as String).getObject("", CreateTodoListCommand::class.java)
        assertThat(command.name).isEqualTo("name")
        assertThat(command.getAggregateUuid()).isNotNull()
        assertThat(command.getTimestamp()).isNotNull()
    }
}

