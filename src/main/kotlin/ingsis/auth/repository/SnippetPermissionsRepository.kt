package ingsis.auth.repository

import ingsis.auth.common.Permissions
import ingsis.auth.entity.SnippetPermissions
import org.springframework.stereotype.Repository

@Repository
class SnippetPermissionsRepository(
    private val repository: SnippetPermissionsJpaRepositoryInterface,
) : SnippetPermissionsRepositoryInterface {
    override fun grantSnippetWriteAccess(snippetPermissions: SnippetPermissions): SnippetPermissions = repository.save(snippetPermissions)

    override fun grantSnippetReadAccess(snippetPermissions: SnippetPermissions): SnippetPermissions = repository.save(snippetPermissions)

    override fun revokeSnippetAccess(
        snippetId: String,
        userId: String,
    ): Boolean {
        val permission = repository.findBySnippetIdAndUserId(snippetId, userId) ?: return false
        repository.delete(permission)
        return true
    }

    override fun findAll(): List<SnippetPermissions> = repository.findAll()

    override fun findBySnippetIdAndUserId(
        snippetId: String,
        userId: String,
    ): SnippetPermissions? = repository.findBySnippetIdAndUserId(snippetId, userId)

    override fun findBySnippetId(snippetId: String): List<SnippetPermissions> = repository.findBySnippetId(snippetId)

    override fun userHasWriteAccess(
        snippetId: String,
        userId: String,
    ): Boolean {
        val permission = repository.findBySnippetIdAndUserId(snippetId, userId) ?: return false
        return permission.permission == Permissions.WRITE
    }

    override fun userHasReadAccess(
        snippetId: String,
        userId: String,
    ): Boolean {
        val permission = repository.findBySnippetIdAndUserId(snippetId, userId) ?: return false
        return permission.permission == Permissions.READ || permission.permission == Permissions.WRITE
    }

    override fun getSharedSnippetIdsByUserId(userId: String): List<String> =
        repository
            .findAll()
            .filter { it.userId == userId && it.permission == Permissions.READ }
            .map { it.snippetId }
}
