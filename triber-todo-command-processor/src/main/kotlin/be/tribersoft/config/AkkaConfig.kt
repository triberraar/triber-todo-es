package be.tribersoft.config

import akka.actor.ActorRef
import akka.actor.ActorSystem
import be.tribersoft.todo_common.akka.SpringExtension
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class ApplicationConfiguration(val applicationContext: ApplicationContext, val springExtension: SpringExtension, val actorSystem: ActorSystem) {

    @Bean
    @Qualifier("supervisorActor")
    open fun superviser(): ActorRef? {
        return actorSystem.actorOf(springExtension.props("supervisorActor"), "supervisorActor")
    }
}
