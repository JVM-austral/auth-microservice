package ingsis.auth.repository

import ingsis.auth.entity.SnippetPermissions
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SnippetPermissionsJpaRepositoryInterface : JpaRepository<SnippetPermissions, String> {
    fun findBySnippetIdAndUserId(
        snippetId: String,
        userId: String,
    ): SnippetPermissions?

    fun findBySnippetId(snippetId: String): List<SnippetPermissions>
}
