package be.tribersoft

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
open class TriberTodoOverviewQueryEndpointApplication

fun main(args: Array<String>) {
    SpringApplication.run(TriberTodoOverviewQueryEndpointApplication::class.java, *args)
}
