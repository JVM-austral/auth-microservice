package ingsis.auth.service

import ingsis.auth.entity.User
import ingsis.auth.exception.UserAlreadyExistsException
import ingsis.auth.exception.UserNotFoundException
import ingsis.auth.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    private val log = LoggerFactory.getLogger(UserService::class.java)

    fun findAll(): List<User> {
        val result = userRepository.findAll()
        log.info("All users fetched, total count: ${result.size}")
        return result
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
}
