package be.tribersoft.rest

import be.tribersoft.service.EventService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping(path = arrayOf("/event"))
class EventController(val eventService: EventService) {
    @GetMapping(path = arrayOf("/{aggregateUuid}"), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun getByAggregateUuid(@PathVariable aggregateUuid: UUID): List<Map<String, Any>> {
        return eventService.findByAggregateUuid(aggregateUuid)
    }

}
