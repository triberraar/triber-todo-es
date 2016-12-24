package be.tribersoft.service

import be.tribersoft.domain.TodoList
import be.tribersoft.domain.TodoListElasticRepository
import be.tribersoft.messaging.TodoListCreatedEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class TodoListService(val todoListElasticRepository: TodoListElasticRepository) {

    private val LOGGER: Logger = LoggerFactory.getLogger(this.javaClass)

    fun on(event: TodoListCreatedEvent) {
        val todoList = TodoList(event.getAggregateUuid().toString(), event.name, event.getTimestamp())
        LOGGER.info("Storing a todolist $todoList")
        todoListElasticRepository.save(todoList)
    }
}