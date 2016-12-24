package be.tribersoft.actor

import akka.actor.ActorRef
import akka.actor.UntypedActorContext
import be.tribersoft.client.EventStoreClientImpl
import be.tribersoft.messaging.Event
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.internal.util.reflection.Whitebox

class EventHandlerInitTest {

    private val eventStoreClient: EventStoreClientImpl = mock(EventStoreClientImpl::class.java)

    private val actor: CommandActor = mock(CommandActor::class.java)

    private val context: UntypedActorContext = mock(UntypedActorContext::class.java)

    private val self: ActorRef = mock(ActorRef::class.java)

    private val event1: Event = mock(Event::class.java)
    private val event2: Event = mock(Event::class.java)

    private val eventHandler: EventHandler = EventHandler(eventStoreClient)

    @Before
    fun setUp() {
        `when`(eventStoreClient.getEvents("uuid")).thenReturn(listOf(event1, event2))
        Whitebox.setInternalState(actor, "id", "uuid")
        Whitebox.setInternalState(actor, "lastProcessed", -1)
        `when`(event1.getOrder()).thenReturn(0L)
        `when`(event2.getOrder()).thenReturn(1L)
    }

    @Test
    fun getsAndAppliesAllEvents() {
        eventHandler.init(actor)

        assertThat(actor.lastProcessed).isEqualTo(1L)
    }

    @Test
    fun stopsActorWhenOutOfSync() {
        Whitebox.setInternalState(actor, "lastProcessed", 2)
        `when`(actor.context).thenReturn(context)
        `when`(actor.self).thenReturn(self)

        eventHandler.init(actor)

        verify(context, atLeast(1)).stop(self)
    }
}