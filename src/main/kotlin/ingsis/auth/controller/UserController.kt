package ingsis.auth.controller

import ingsis.auth.dto.PaginatedUserResponse
import ingsis.auth.entity.User
import ingsis.auth.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
) {
    private val log = LoggerFactory.getLogger(UserController::class.java)

    @GetMapping
    fun getAll(): List<User> {
        log.info("Fetching all users")
        return userService.findAll()
    }

    @GetMapping("/paginated")
    fun getAll(
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "page_size", defaultValue = "10") size: Int,
        @RequestParam(required = false) filter: String?,
    ): PaginatedUserResponse {
        log.info("Fetching all users")
        return userService.findPaginatedUserWithNameFilter(
            filter = filter,
            page = page,
            pageSize = size,
        )
    }

    @PostMapping
    fun createUser(
        @RequestBody user: User,
    ): User {
        log.info("Creating user with id: ${user.id}")
        return userService.save(user)
    }

    @GetMapping("/{id}")
    fun getUserById(
        @PathVariable id: String,
    ): User {
        log.info("Fetching user with id: $id")
        return userService.findById(id)
    }
}
