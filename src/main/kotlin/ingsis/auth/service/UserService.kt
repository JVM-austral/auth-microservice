package ingsis.auth.service

import ingsis.auth.entity.User
import ingsis.auth.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    fun findAll(): List<User> = userRepository.findAll()

    fun save(user: User): User = userRepository.save(user)

    fun findById(id: String): User? = userRepository.findById(id).orElse(null)
}
