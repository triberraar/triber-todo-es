package be.tribersoft.messaging

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.LocalDateTime
import java.util.*


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(JsonSubTypes.Type(name = TodoListCreatedEvent.TYPE, value = TodoListCreatedEvent::class),
        JsonSubTypes.Type(name = TodoListUpdatedEvent.TYPE, value = TodoListUpdatedEvent::class))
interface Event {

    fun getAggregateUuid(): UUID

    fun getTimestamp(): LocalDateTime

    fun getOrder(): Long
}

data class TodoListCreatedEvent(val name: String, private val aggregateUuid: UUID, private val order: Long, private val timestamp: LocalDateTime = LocalDateTime.now()) : Event {

    override fun getOrder() = order

    override fun getAggregateUuid() = aggregateUuid

    override fun getTimestamp() = timestamp

    companion object {
        const val TYPE = "event.todolist.created"
    }
}

data class TodoListUpdatedEvent(val name: String, private val aggregateUuid: UUID, private val order: Long, private val timestamp: LocalDateTime = LocalDateTime.now()) : Event {

    override fun getOrder() = order

    override fun getAggregateUuid() = aggregateUuid

    override fun getTimestamp() = timestamp

    companion object {
        const val TYPE = "event.todolist.updated"
    }
}