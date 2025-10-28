package ingsis.auth.repository

import ingsis.auth.entity.SnippetPermissions
import org.springframework.stereotype.Repository

@Repository
class SnippetPermissionsRepository(
    private val repository: SnippetPermissionsRepositoryInterface,
) {
    fun grantSnippetWriteAccess(snippetPermissions: SnippetPermissions): SnippetPermissions = repository.save(snippetPermissions)

    fun grantSnippetReadAccess(snippetPermissions: SnippetPermissions): SnippetPermissions = repository.save(snippetPermissions)

    fun revokeSnippetAccess(
        snippetId: String,
        userId: String,
    ): Boolean {
        val permission = repository.findBySnippetIdAndUserId(snippetId, userId) ?: return false
        repository.delete(permission)
        return true
    }

    fun findAll(): List<SnippetPermissions> = repository.findAll()

    fun findBySnippetIdAndUserId(
        snippetId: String,
        userId: String,
    ): SnippetPermissions? = repository.findBySnippetIdAndUserId(snippetId, userId)

    fun findBySnippetId(snippetId: String): List<SnippetPermissions> = repository.findBySnippetId(snippetId)
}
