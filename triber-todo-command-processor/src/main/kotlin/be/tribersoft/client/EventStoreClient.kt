package be.tribersoft.client

import be.tribersoft.messaging.Event
import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod


@FeignClient("triber-todo-event-store")
interface EventStoreClient {
    @RequestMapping(method = arrayOf(RequestMethod.GET), value = "/event/{aggregateUuid}")
    fun getEvents(@PathVariable("aggregateUuid") aggregateUuid: String): List<Event>
}

@Component
open class EventStoreClientImpl(val eventStoreClient: EventStoreClient) {
    open fun getEvents(aggregateUuid: String) = eventStoreClient.getEvents(aggregateUuid)
}