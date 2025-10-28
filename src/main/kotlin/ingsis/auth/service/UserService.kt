package ingsis.auth.service

import ingsis.auth.entity.User
import ingsis.auth.exception.UserAlreadyExistsException
import ingsis.auth.exception.UserNotFoundException
import ingsis.auth.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    fun findAll(): List<User> = userRepository.findAll()

    fun save(user: User): User {
        if (userRepository.existsById(user.id)) {
            throw UserAlreadyExistsException("User already exists with id: ${user.id}")
        }
        return userRepository.save(user)
    }

    fun findById(id: String): User =
        userRepository.findById(id).orElseThrow {
            UserNotFoundException("User not found with id: $id")
        }

    fun findByIdOrNull(id: String): User? = userRepository.findById(id).orElse(null)
}
