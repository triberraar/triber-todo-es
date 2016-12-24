package be.tribersoft.rest

import be.tribersoft.domain.Aggregate
import be.tribersoft.domain.AggregateRepository
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = arrayOf("/aggregate"))
class AggregateController(val aggregateRepository: AggregateRepository) {

    @GetMapping(produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun getAllAggregates(): MutableList<Aggregate>? {
        return aggregateRepository.findAll()
    }
}
