package ingsis.auth.controller

import ingsis.auth.entity.User
import ingsis.auth.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
) {
    @GetMapping
    fun getAll(): List<User> = userService.findAll()

    @PostMapping
    fun createUser(
        @RequestBody user: User,
    ): User = userService.save(user)

    @GetMapping("/{id}")
    fun getUserById(
        @PathVariable id: String,
    ): User = userService.findById(id)
}
