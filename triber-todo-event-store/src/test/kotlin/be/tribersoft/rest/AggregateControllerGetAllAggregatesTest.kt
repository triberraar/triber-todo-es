package be.tribersoft.rest

import be.tribersoft.domain.Aggregate
import be.tribersoft.domain.AggregateRepository
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers
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

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AggregateControllerGetAllAggregatesTest() {
    @Value("\${local.server.port}")
    private lateinit var serverPort: Integer

    @Autowired
    private lateinit var aggregateRepository: AggregateRepository

    private val aggregate1 = Aggregate()
    private val aggregate2 = Aggregate()

    @After
    fun cleanUp() {
        aggregateRepository.deleteAll()
    }

    @Before
    fun setUp() {
        RestAssured.port = serverPort.toInt();

        aggregateRepository.save(aggregate1)
        aggregateRepository.save(aggregate2)
    }

    @Test
    fun returnsAllAggregates() {
        RestAssured.given()
                .accept(ContentType.JSON)
                .`when`()
                .get("/aggregate")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", Matchers.`is`(2))
                .body("[0].size()", Matchers.`is`(1))
                .body("[0].aggregateUuid", Matchers.`is`(aggregate1.aggregateUuid))
                .body("[1].size()", Matchers.`is`(1))
                .body("[1].aggregateUuid", Matchers.`is`(aggregate2.aggregateUuid))
    }
}