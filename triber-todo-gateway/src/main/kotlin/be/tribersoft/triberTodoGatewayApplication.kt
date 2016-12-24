package be.tribersoft

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.cloud.netflix.zuul.EnableZuulProxy

@SpringBootApplication
@EnableEurekaClient
@EnableZuulProxy
open class TriberTodoGatewayApplication

fun main(args: Array<String>) {
    SpringApplication.run(TriberTodoGatewayApplication::class.java, *args)
}
