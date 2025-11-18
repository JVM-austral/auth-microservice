package ingsis.auth.service

import ingsis.auth.dto.PaginatedUserResponse
import ingsis.auth.dto.UserForResponse
import ingsis.auth.entity.User
import ingsis.auth.exception.UserAlreadyExistsException
import ingsis.auth.exception.UserNotFoundException
import ingsis.auth.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val OAuthService: OAuthService,
) {
    private val log = LoggerFactory.getLogger(UserService::class.java)

    fun findAll(): List<User> {
        val result = userRepository.findAll()
        log.info("All users fetched, total count: ${result.size}")
        return result
    }

    fun findPaginatedUserWithNameFilter(
        filter: String?,
        page: Int,
        pageSize: Int,
    ): PaginatedUserResponse {
        val result = userRepository.findAll()
        val responseResult = mapToUserForResponseList(result)
        log.info("All users fetched, total count: ${result.size}")
        val users = mapToUserForResponseList(result)
        val fromIndex = page * pageSize
        val toIndex = minOf(fromIndex + pageSize, users.size)
        val subset = responseResult.slice(fromIndex until toIndex)
        return PaginatedUserResponse(
            users = subset,
            count = result.size,
            page = page,
            pageSize = pageSize,
        )
    }

    fun save(user: User): User {
        log.info("Attempting to save user with id: ${user.id}")
        if (userRepository.existsById(user.id)) {
            throw UserAlreadyExistsException("User already exists with id: ${user.id}")
        }
        val result = userRepository.save(user)
        log.info("Successfully saved user with id: ${user.id}")
        return result
    }

    fun findById(id: String): User {
        log.info("Finding user by id: $id")

        return userRepository.findById(id).orElseThrow {
            UserNotFoundException("User not found with id: $id")
        }
    }

    private fun mapToUserForResponse(user: User): UserForResponse {
        val email = OAuthService.getUserEmail(user.id)
        log.info("Mapping User entity to UserForResponse for user id: ${user.id} with name: $email")
        return UserForResponse(
            id = user.id,
            email = email,
        )
    }

    private fun mapToUserForResponseList(users: List<User>): List<UserForResponse> = users.map { mapToUserForResponse(it) }
}
