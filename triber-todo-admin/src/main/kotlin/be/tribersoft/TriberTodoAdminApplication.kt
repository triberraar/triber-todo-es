package be.tribersoft

import de.codecentric.boot.admin.config.EnableAdminServer
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableAdminServer
@EnableDiscoveryClient
open class TriberTodoAdminApplication

fun main(args: Array<String>) {
    SpringApplication.run(TriberTodoAdminApplication::class.java, *args)
}
