package ingsis.auth.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "friends")
data class Friend(
    @Id
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val friendId: String,
)
