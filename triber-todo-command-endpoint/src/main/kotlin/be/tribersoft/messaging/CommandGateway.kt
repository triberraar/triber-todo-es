package be.tribersoft.messaging

import org.springframework.cloud.stream.annotation.Output
import org.springframework.integration.annotation.Gateway
import org.springframework.integration.annotation.MessagingGateway
import org.springframework.messaging.MessageChannel

interface CommandChannel {

    @Output
    fun command(): MessageChannel

    companion object {
        const val CHANNEL_NAME = "command"
    }
}

@MessagingGateway
interface CommandGateway {
    @Gateway(requestChannel = CommandChannel.CHANNEL_NAME)
    fun create(command: CreateTodoListCommand)

    @Gateway(requestChannel = CommandChannel.CHANNEL_NAME)
    fun update(command: UpdateTodoListCommand)
}