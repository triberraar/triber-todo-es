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
class TodoListControllerAllTest {

    @Value("\${local.server.port}")
    private lateinit var serverPort: Integer

    @Autowired
    private lateinit var elasticRepository: TodoListElasticRepository

    @Autowired
    private lateinit var elasticsearchTemplate: ElasticsearchTemplate


    private val todoList1 = TodoList("uuid1", "name1", LocalDateTime.now())
    private val todoList2 = TodoList("uuid2", "name2", LocalDateTime.now())

    @Before
    fun setUp() {
        RestAssured.port = serverPort.toInt();

        elasticsearchTemplate.deleteIndex(TodoList::class.java)
        elasticsearchTemplate.createIndex(TodoList::class.java)
        elasticsearchTemplate.refresh(TodoList::class.java)

        elasticRepository.save(todoList1)
        elasticRepository.save(todoList2)
    }

    @Test
    fun returnsAllTodoLists() {
        RestAssured.given()
                .accept(ContentType.JSON)
                .`when`()
                .get("/todo-list")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", Matchers.`is`(2))
                .body("[0].size()", Matchers.`is`(3))
                .body("[0].uuid", Matchers.`is`("uuid2"))
                .body("[0].name", Matchers.`is`("name2"))
                .body("[0].created", Matchers.`is`(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").format(todoList2.created).trim('0')))
                .body("[1].size()", Matchers.`is`(3))
                .body("[1].uuid", Matchers.`is`("uuid1"))
                .body("[1].name", Matchers.`is`("name1"))
                .body("[1].created", Matchers.`is`(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").format(todoList1.created).trim('0')))
    }

}