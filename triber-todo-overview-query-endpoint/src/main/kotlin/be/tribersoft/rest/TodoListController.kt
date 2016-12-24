package be.tribersoft.rest

import be.tribersoft.domain.TodoList
import be.tribersoft.domain.TodoListRepository
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = arrayOf("/todo-list"))
class TodoListController(val todoListRepository: TodoListRepository) {
    @GetMapping(produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun all(): List<TodoList> {
        val todoLists = todoListRepository.all()
        return todoLists.map { it -> it }
    }

    @GetMapping(path = arrayOf("/{uuid}"), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun one(@PathVariable("uuid") uuid: String) = todoListRepository.get(uuid)
}

