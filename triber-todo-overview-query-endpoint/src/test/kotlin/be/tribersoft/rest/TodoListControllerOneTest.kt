package be.tribersoft.rest

import be.tribersoft.domain.TodoList
import be.tribersoft.domain.TodoListElasticRepository
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TodoListControllerOneTest {

    @Value("\${local.server.port}")
    private lateinit var serverPort: Integer

    @Autowired
    private lateinit var elasticRepository: TodoListElasticRepository

    @Autowired
    private lateinit var elasticsearchTemplate: ElasticsearchTemplate

    private val todoList = TodoList("uuid", "name", LocalDateTime.now())

    @Before
    fun setUp() {
        RestAssured.port = serverPort.toInt();

        elasticsearchTemplate.deleteIndex(TodoList::class.java)
        elasticsearchTemplate.createIndex(TodoList::class.java)
        elasticsearchTemplate.refresh(TodoList::class.java)

        elasticRepository.save(todoList)
    }

    @Test
    fun returnsTodoList() {
        RestAssured.given()
                .accept(ContentType.JSON)
                .pathParam("uuid", "uuid")
                .`when`()
                .get("/todo-list/{uuid}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", Matchers.`is`(3))
                .body("uuid", Matchers.`is`("uuid"))
                .body("name", Matchers.`is`("name"))
                .body("created", Matchers.`is`(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").format(todoList.created).trim('0')))
    }

    @Test
    fun failsWhenNotFound() {
        RestAssured.given()
                .accept(ContentType.JSON)
                .pathParam("uuid", "nonexisting")
                .`when`()
                .get("/todo-list/{uuid}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", Matchers.`is`("todo list not found"))
    }

}