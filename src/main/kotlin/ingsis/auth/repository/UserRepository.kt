package ingsis.auth.repository

import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.JpaRepository
import ingsis.auth.entity.User

@Repository
interface UserRepository : JpaRepository<User, String> {

}