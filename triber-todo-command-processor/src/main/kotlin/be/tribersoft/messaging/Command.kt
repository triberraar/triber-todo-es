package be.tribersoft.messaging

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.LocalDateTime
import java.util.*

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(JsonSubTypes.Type(name = CreateTodoListCommand.TYPE, value = CreateTodoListCommand::class),
        JsonSubTypes.Type(name = UpdateTodoListCommand.TYPE, value = UpdateTodoListCommand::class))
interface Command {

    fun getAggregateUuid(): UUID

    fun getTimestamp(): LocalDateTime
}

data class CreateTodoListCommand(val name: String, private val aggregateUuid: UUID, private val timestamp: LocalDateTime = LocalDateTime.now()) : Command {

    override fun getAggregateUuid() = aggregateUuid

    override fun getTimestamp() = timestamp

    companion object {
        const val TYPE = "command.todolist.create"
    }
}

data class UpdateTodoListCommand(val name: String, private val aggregateUuid: UUID, private val timestamp: LocalDateTime = LocalDateTime.now()) : Command {

    override fun getAggregateUuid() = aggregateUuid

    override fun getTimestamp() = timestamp

    companion object {
        const val TYPE = "command.todolist.update"
    }
}