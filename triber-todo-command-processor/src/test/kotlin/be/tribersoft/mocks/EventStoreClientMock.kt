package be.tribersoft.mocks

import be.tribersoft.client.EventStoreClientImpl
import org.mockito.Mockito.mock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class EventStoreClientMock {

    @Bean
    open fun eventStoreClient() = mock(EventStoreClientImpl::class.java)

}