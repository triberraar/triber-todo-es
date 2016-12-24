package be.tribersoft.todo_common.akka

import akka.actor.*
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

class SpringActorProducer(private val applicationContext: ApplicationContext, private val actorBeanName: String, vararg args: Any) : IndirectActorProducer {

    private val LOGGER: Logger = LoggerFactory.getLogger(this.javaClass)
    private val id: String?

    init {
        id = if (args.isEmpty()) null else args[0] as String
    }

    override fun produce(): Actor {
        if (id == null) {
            LOGGER.info("Creating an actor without an id")
            return applicationContext.getBean(actorBeanName) as Actor
        } else {
            LOGGER.info("Creating an actor with id $id")
            return applicationContext.getBean(actorBeanName, id) as Actor
        }
    }

    override fun actorClass(): Class<out Actor> {
        return applicationContext.getType(actorBeanName) as Class<out Actor>
    }
}

@Component
class SpringExtension : Extension {

    private var applicationContext: ApplicationContext? = null

    fun initialize(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    fun props(actorBeanName: String): Props {
        return Props.create(SpringActorProducer::class.java, applicationContext, actorBeanName, emptyArray<Any>())
    }

    fun props(actorBeanName: String, vararg args: Any): Props {
        return Props.create(SpringActorProducer::class.java, applicationContext, actorBeanName, args)
    }
}

@Configuration
open class AkkaActorConfiguration(val applicationContext: ApplicationContext, val springExtension: SpringExtension) {

    @Value("\${tribersoft.akka.actorsystem.name:triberActorSystem}")
    lateinit var actorSystemName: String

    @Bean(destroyMethod = "shutdown")
    open fun actorSystem(): ActorSystem {
        val actorSystem = ActorSystem.create(actorSystemName, akkaConfiguration())
        springExtension.initialize(applicationContext)
        return actorSystem
    }

    @Bean
    open fun akkaConfiguration(): Config {
        return ConfigFactory.load()
    }

}