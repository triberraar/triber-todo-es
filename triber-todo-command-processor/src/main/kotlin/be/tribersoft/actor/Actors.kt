package be.tribersoft.actor

import akka.actor.ActorRef
import akka.actor.UntypedActor
import be.tribersoft.client.EventStoreClientImpl
import be.tribersoft.messaging.*
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
        if (message is Command) {
            createOrGetControllerActor(message.getAggregateUuid()).forward(message, context)
        } else {
            unhandled(message);
        }
    }

    fun createOrGetControllerActor(id: UUID): ActorRef {
        val actorRef = context.getChild("commandActor$id")
        if (actorRef != null) {
            return actorRef
        }
        val childActor = context.actorOf(springExtension.props("commandActor", id.toString()), "commandActor$id")
        context.watch(childActor)
        childActor.tell(InitMessage(), self)
        return childActor
    }
}

@Component("commandActor")
@Scope("prototype")
open class CommandActor(val id: String) : UntypedActor() {

    private val LOGGER: Logger = LoggerFactory.getLogger(this.javaClass)

    var lastProcessed = -1L

    @Autowired
    lateinit var eventGateway: EventGateway

    @Autowired
    lateinit var eventHandler: EventHandler

    override fun onReceive(message: Any?) {
        LOGGER.info("Actor $id received a message $message")
        when (message) {
            is InitMessage -> eventHandler.init(this)
            is CreateTodoListCommand -> handle(TodoListCreatedEvent(message.name, message.getAggregateUuid(), lastProcessed + 1))
            is UpdateTodoListCommand -> handle(TodoListUpdatedEvent(message.name, message.getAggregateUuid(), lastProcessed + 1))
            else -> unhandled(message)
        }
    }

    fun handle(event: Event) {
        LOGGER.info("Emitting todo list  event: $event")
        eventGateway.emit(event)
        eventHandler.on(this, event)
    }
}

@Component
class EventHandler(val eventStoreClient: EventStoreClientImpl) {
    private val LOGGER: Logger = LoggerFactory.getLogger(this.javaClass)

    fun init(actor: CommandActor) {
        for (event in eventStoreClient.getEvents(actor.id)) {
            on(actor, event);
        }
    }

    fun on(actor: CommandActor, event: Event) {
        LOGGER.info("Processing event: $event")
        if (actor.lastProcessed != event.getOrder() - 1) {
            LOGGER.error("Actor seems out of sync: $actor vs $event")
            actor.context.stop(actor.self)
        } else {
            actor.lastProcessed = event.getOrder()
        }
    }
}