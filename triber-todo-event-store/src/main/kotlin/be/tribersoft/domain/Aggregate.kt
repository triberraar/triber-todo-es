package be.tribersoft.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

@Document
data class Aggregate(@Id val aggregateUuid: String = UUID.randomUUID().toString())

interface AggregateRepository : MongoRepository<Aggregate, String> {

}