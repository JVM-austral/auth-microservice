package ingsis.auth.entity

import ingsis.auth.common.Permissions
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table

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
