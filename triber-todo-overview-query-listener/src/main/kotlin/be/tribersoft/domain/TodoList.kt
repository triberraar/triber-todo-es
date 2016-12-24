package be.tribersoft.domain

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository
import java.time.LocalDateTime

@Document(indexName = "todo-list")
class TodoList(@Id val uuid: String, var name: String, val created: LocalDateTime)

interface TodoListElasticRepository : ElasticsearchCrudRepository<TodoList, String> {}
