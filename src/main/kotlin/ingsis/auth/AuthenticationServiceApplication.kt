package ingsis.auth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@SpringBootApplication
class AuthenticationServiceApplication {
    @GetMapping("/")
    fun index(): String = "I'm Alive!"
}

fun main(args: Array<String>) {
    runApplication<AuthenticationServiceApplication>(*args)
}
