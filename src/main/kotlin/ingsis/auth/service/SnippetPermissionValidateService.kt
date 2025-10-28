package ingsis.auth.service

import ingsis.auth.dto.PermissionValidationResponse
import ingsis.auth.dto.SnippetPermissionRequest
import ingsis.auth.exception.UnauthorizedPermissionActionException
import ingsis.auth.repository.SnippetPermissionsRepository
import org.springframework.stereotype.Service

@Service
class SnippetPermissionValidateService(
    private val snippetPermissionsRepository: SnippetPermissionsRepository,
) {
    fun validateSnippetWriteAccess(
        snippetPermissionRequest: SnippetPermissionRequest,
        requestingUserId: String,
    ): PermissionValidationResponse {
        val targetUserId = resolveUserId(snippetPermissionRequest, requestingUserId)
        val hasAccess =
            snippetPermissionsRepository.userHasWriteAccess(
                snippetPermissionRequest.snippetId,
                targetUserId,
            )
        return if (hasAccess) {
            PermissionValidationResponse(true)
        } else {
            throw UnauthorizedPermissionActionException(
                "User $targetUserId does not have WRITE permission to grant access to snippet: ${snippetPermissionRequest.snippetId}",
            )
        }
    }

    fun validateSnippetReadAccess(
        snippetPermissionRequest: SnippetPermissionRequest,
        requestingUserId: String,
    ): PermissionValidationResponse {
        val targetUserId = resolveUserId(snippetPermissionRequest, requestingUserId)
        val hasAccess =
            snippetPermissionsRepository.userHasReadAccess(
                snippetPermissionRequest.snippetId,
                targetUserId,
            )
        return if (hasAccess) {
            PermissionValidationResponse(true)
        } else {
            throw UnauthorizedPermissionActionException(
                "User $targetUserId has no read access to snippet ${snippetPermissionRequest.snippetId}",
            )
        }
    }

    private fun resolveUserId(
        snippetPermissionRequest: SnippetPermissionRequest,
        requestingUserId: String,
    ): String = snippetPermissionRequest.userId ?: requestingUserId
}
