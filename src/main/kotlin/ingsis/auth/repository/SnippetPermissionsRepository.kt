package ingsis.auth.repository

import ingsis.auth.entity.SnippetPermissions
import org.springframework.stereotype.Repository

@Repository
class SnippetPermissionsRepository(
    private val jpaRepo: SnippetPermissionsRepositoryInterface,
) {
    fun create(snippetPermission: SnippetPermissions): SnippetPermissions =
        jpaRepo.save(snippetPermission)

    fun findBySnippetId(snippetId: String): List<SnippetPermissions> =
        jpaRepo.findBySnippetId(snippetId)

    fun findByUserId(userId: String): List<SnippetPermissions> =
        jpaRepo.findByUserId(userId)
}
