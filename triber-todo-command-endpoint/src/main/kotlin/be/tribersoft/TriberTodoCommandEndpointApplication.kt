package be.tribersoft

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
open class TriberTodoCommandEndpointApplication

fun main(args: Array<String>) {
    SpringApplication.run(TriberTodoCommandEndpointApplication::class.java, *args)
}
