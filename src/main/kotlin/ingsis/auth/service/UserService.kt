package ingsis.auth.service

import org.springframework.stereotype.Service
import ingsis.auth.entity.User
import ingsis.auth.repository.UserRepository

@Service
class UserService(private val userRepository: UserRepository) {

    fun findAll(): List<User> = userRepository.findAll()

    fun save(user: User): User = userRepository.save(user)

}