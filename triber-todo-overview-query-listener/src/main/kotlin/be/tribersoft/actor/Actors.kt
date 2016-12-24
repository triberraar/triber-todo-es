package be.tribersoft.actor

import akka.actor.ActorRef
import akka.actor.UntypedActor
import be.tribersoft.messaging.Event
import be.tribersoft.messaging.TodoListCreatedEvent
import be.tribersoft.service.TodoListService
import be.tribersoft.todo_common.akka.SpringExtension
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import java.util.*

@Component("supervisorActor")
@Scope("prototype")
class SupervisorActor() : UntypedActor() {

    @Autowired
    lateinit var springExtension: SpringExtension

    override fun onReceive(message: Any?) {
        if (message is Event) {
            createOrGetControllerActor(message.getAggregateUuid()).forward(message, context)
        } else {
            unhandled(message);
        }
    }

    fun createOrGetControllerActor(id: UUID): ActorRef {
        val actorRef = context.getChild("eventActor$id")
        if (actorRef != null) {
            return actorRef
        }
        val childActor = context.actorOf(springExtension.props("eventActor", id.toString()), "eventActor$id")
        context.watch(childActor)
        return childActor
    }
}

@Component("eventActor")
@Scope("prototype")
class EventActor(val id: String) : UntypedActor() {

    private val LOGGER: Logger = LoggerFactory.getLogger(this.javaClass)

    var lastProcessed = 0L

    @Autowired
    lateinit var todoListService: TodoListService

    override fun onReceive(message: Any?) {
        LOGGER.info("Actor with id $id received a message: $message")
        if (message is TodoListCreatedEvent) {
            todoListService.on(message)
            lastProcessed = 0L
        }
        unhandled(message)
    }
}