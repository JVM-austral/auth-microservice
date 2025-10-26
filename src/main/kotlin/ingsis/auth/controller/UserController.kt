package ingsis.auth.controller

import org.springframework.web.bind.annotation.*
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import ingsis.auth.entity.User
import ingsis.auth.service.UserService

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @GetMapping
    fun getAll(): List<User> = userService.findAll()

    @PostMapping
    fun createUser(@RequestBody user: User): User = userService.save(user)

}