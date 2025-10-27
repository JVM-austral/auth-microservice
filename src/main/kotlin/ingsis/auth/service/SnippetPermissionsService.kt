package ingsis.auth.service

import ingsis.auth.entity.SnippetPermissions
import ingsis.auth.repository.SnippetPermissionsRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SnippetPermissionsService(
    private val snippetPermissionsRepository: SnippetPermissionsRepository,
) {
    fun createSnippetPermission(
        snippetId: String,
        userId: String,
        permission: String,
    ): SnippetPermissions {
        val snippetPermission =
            SnippetPermissions(
                id = UUID.randomUUID().toString(),
                snippetId = snippetId,
                userId = userId,
                permission = enumValueOf(permission.uppercase()),
            )
        return snippetPermissionsRepository.create(snippetPermission)
    }

    fun getBySnippet(snippetId: String) = snippetPermissionsRepository.findBySnippetId(snippetId)

    fun getByUser(userId: String) = snippetPermissionsRepository.findByUserId(userId)
}
