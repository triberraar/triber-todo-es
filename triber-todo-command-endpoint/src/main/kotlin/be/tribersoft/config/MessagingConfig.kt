package be.tribersoft.config

import be.tribersoft.messaging.CommandChannel
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.IntegrationComponentScan

@Configuration
@EnableBinding(CommandChannel::class)
@IntegrationComponentScan("be.tribersoft.messaging")
open class MessagingConfig() {

}
