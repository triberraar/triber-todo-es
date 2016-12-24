package be.tribersoft.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.LocalDateTime
import java.util.*

@Document()
data class Event(val type: String = "", val aggregateUuid: String = "", val order: Long = 0L, val timestamp: LocalDateTime = LocalDateTime.now(), val content: String = "", @Id val id: String = UUID.randomUUID().toString())

interface EventRepository : MongoRepository<Event, String> {
    fun findByAggregateUuidOrderByOrderAsc(aggregateUuid: String): List<Event>

}
