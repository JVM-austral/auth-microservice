package ingsis.auth.entity

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table

enum class Permissions {
    READ,
    WRITE,
}

@Entity
@Table(name = "snippet_permissions")
data class SnippetPermissions(
    @Id
    val id: String,
    val snippetId: String,
    val userId: String,
    @Enumerated(EnumType.STRING)
    val permission: Permissions,
)
