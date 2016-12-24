package be.tribersoft.domain

import be.tribersoft.exception.TodoListNotFoundException
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Sort
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Document(indexName = "todo-list")
class TodoList(@Id val uuid: String, var name: String, val created: LocalDateTime)

interface TodoListElasticRepository : ElasticsearchCrudRepository<TodoList, String> {
}

@Component
open class TodoListRepository(val todoListElasticRepository: TodoListElasticRepository, val elasticsearchTemplate: ElasticsearchTemplate) {

    fun get(uuid: String) = todoListElasticRepository.findOne(uuid) ?: throw TodoListNotFoundException()

    fun all() = todoListElasticRepository.findAll(Sort(Sort.Direction.DESC, "created"))
}