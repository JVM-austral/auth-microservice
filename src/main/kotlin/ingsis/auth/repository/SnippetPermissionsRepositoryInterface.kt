package ingsis.auth.repository

import ingsis.auth.entity.SnippetPermissions
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SnippetPermissionsRepositoryInterface : JpaRepository<SnippetPermissions, String> {
    fun findBySnippetId(snippetId: String): List<SnippetPermissions>

    fun findByUserId(userId: String): List<SnippetPermissions>
}
