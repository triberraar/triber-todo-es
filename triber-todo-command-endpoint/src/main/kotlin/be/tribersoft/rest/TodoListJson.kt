package be.tribersoft.rest

import java.util.*

data class TodoListJson(val uuid: UUID)

data class CreateTodoListJson(val name: String)
data class UpdateTodoListJson(val name: String)