package be.tribersoft

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@SpringBootApplication
@EnableEurekaServer
open class TriberTodoDiscoveryApplication

fun main(args: Array<String>) {
    SpringApplication.run(TriberTodoDiscoveryApplication::class.java, *args)
}
