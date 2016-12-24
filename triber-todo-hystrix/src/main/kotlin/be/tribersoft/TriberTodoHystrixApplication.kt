package be.tribersoft

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@SpringBootApplication
@EnableDiscoveryClient
@EnableHystrixDashboard
open class TriberTodoDiscoveryApplication

fun main(args: Array<String>) {
    SpringApplication.run(TriberTodoDiscoveryApplication::class.java, *args)
}

@Controller
class RedirectController {
    @RequestMapping(path = arrayOf("/"), method = arrayOf(RequestMethod.GET))
    fun home(): String = "redirect:/hystrix"
}