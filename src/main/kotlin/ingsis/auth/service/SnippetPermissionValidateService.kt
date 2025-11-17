package ingsis.auth.service

import ingsis.auth.dto.PermissionValidationResponse
import ingsis.auth.dto.SnippetPermissionRequest
import ingsis.auth.exception.UnauthorizedPermissionActionException
import ingsis.auth.repository.SnippetPermissionsRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SnippetPermissionValidateService(
    private val snippetPermissionsRepository: SnippetPermissionsRepository,
) {
    private val log = LoggerFactory.getLogger(SnippetPermissionValidateService::class.java)

    fun validateSnippetWriteAccess(
        snippetPermissionRequest: SnippetPermissionRequest,
        requestingUserId: String,
    ): PermissionValidationResponse {
        val targetUserId = resolveUserId(snippetPermissionRequest, requestingUserId)
        log.debug("Validating write access for user $targetUserId on snippet ${snippetPermissionRequest.snippetId}")

        val hasAccess =
            snippetPermissionsRepository.userHasWriteAccess(
                snippetPermissionRequest.snippetId,
                targetUserId,
            )

        return if (hasAccess) {
            log.info("Write access granted for user $targetUserId on snippet ${snippetPermissionRequest.snippetId}")
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
            log.info("Read access granted for user $targetUserId on snippet ${snippetPermissionRequest.snippetId}")
            PermissionValidationResponse(true)
        } else {
            throw UnauthorizedPermissionActionException(
                "User $targetUserId has no read access to snippet ${snippetPermissionRequest.snippetId}",
            )
        }
    }

    fun getSharedSnippets(
        userId: String,
    ): List<String> {
        log.debug("Fetching shared snippets for user $userId")
        return snippetPermissionsRepository.getSharedSnippetIdsByUserId(userId)
    }

    private fun resolveUserId(
        snippetPermissionRequest: SnippetPermissionRequest,
        requestingUserId: String,
    ): String = snippetPermissionRequest.userId ?: requestingUserId
}
