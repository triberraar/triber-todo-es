package be.tribersoft.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

class TodoListNotFoundException(message: String = "todo list not found") : RuntimeException(message)

data class errorJson(val message: String)

@ControllerAdvice
class TodoListNotFoundExceptionHandler {

    @ExceptionHandler(TodoListNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    fun handle(ex: TodoListNotFoundException) = errorJson(ex.message!!)
}