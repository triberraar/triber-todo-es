package be.tribersoft.rest

import be.tribersoft.messaging.CommandGateway
import be.tribersoft.messaging.CreateTodoListCommand
import be.tribersoft.messaging.UpdateTodoListCommand
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping(path = arrayOf("/todo-list"))
class TodoListController(private val commandGateway: CommandGateway) {
    private val LOGGER: Logger = LoggerFactory.getLogger(this.javaClass)

    @PostMapping(consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun create(@RequestBody body: CreateTodoListJson): TodoListJson {
        LOGGER.info("Received rest call to create a todo list: $body")
        val uuid = UUID.randomUUID()
        val command = CreateTodoListCommand(body.name, uuid)
        LOGGER.info("Emitting a create todo list command: $command")
        commandGateway.create(command)
        return TodoListJson(uuid)
    }

    @PutMapping(path = arrayOf("/{uuid}"), consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun update(@PathVariable uuid: UUID, @RequestBody body: UpdateTodoListJson): TodoListJson {
        LOGGER.info("Received rest call to update a todo list($uuid): $body")
        val command = UpdateTodoListCommand(body.name, uuid)
        LOGGER.info("Emitting a update todo list command: $command")
        commandGateway.update(command)
        return TodoListJson(uuid)
    }
}