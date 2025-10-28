package ingsis.auth.service

import ingsis.auth.common.Permissions
import ingsis.auth.dto.SnippetPermissionRequest
import ingsis.auth.entity.SnippetPermissions
import ingsis.auth.exception.PermissionAlreadyExistsException
import ingsis.auth.exception.PermissionNotFoundException
import ingsis.auth.exception.SelfRevocationNotAllowedException
import ingsis.auth.exception.UnauthorizedPermissionActionException
import ingsis.auth.repository.SnippetPermissionsRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SnippetPermissionGrantService(
    private val snippetPermissionsRepository: SnippetPermissionsRepository,
) {
    fun grantSnippetWriteAccess(
        snippetPermissionRequest: SnippetPermissionRequest,
        requestingUserId: String,
    ): SnippetPermissions {
        val targetUserId = resolveUserId(snippetPermissionRequest, requestingUserId)
        validateUserCanGrantPermission(snippetPermissionRequest.snippetId, requestingUserId)
        validatePermissionDoesNotExist(snippetPermissionRequest.snippetId, targetUserId)
        val snippetPermission =
            SnippetPermissions(
                id = UUID.randomUUID().toString(),
                snippetId = snippetPermissionRequest.snippetId,
                userId = targetUserId,
                permission = Permissions.WRITE,
            )
        return snippetPermissionsRepository.grantSnippetWriteAccess(snippetPermission)
    }

    fun grantSnippetReadAccess(
        snippetPermissionRequest: SnippetPermissionRequest,
        requestingUserId: String,
    ): SnippetPermissions {
        val targetUserId = resolveUserId(snippetPermissionRequest, requestingUserId)
        validateUserCanGrantPermission(snippetPermissionRequest.snippetId, requestingUserId)
        validatePermissionDoesNotExist(snippetPermissionRequest.snippetId, targetUserId)
        val snippetPermission =
            SnippetPermissions(
                id = UUID.randomUUID().toString(),
                snippetId = snippetPermissionRequest.snippetId,
                userId = targetUserId,
                permission = Permissions.READ,
            )
        return snippetPermissionsRepository.grantSnippetReadAccess(snippetPermission)
    }

    fun revokeSnippetAccess(
        snippetPermissionRequest: SnippetPermissionRequest,
        requestingUserId: String,
    ): Boolean {
        val targetUserId = resolveUserId(snippetPermissionRequest, requestingUserId)
        validateUserCanRevokePermission(snippetPermissionRequest.snippetId, requestingUserId)
        if (targetUserId == requestingUserId) {
            throw SelfRevocationNotAllowedException(
                "You cannot revoke your own permissions for snippet: ${snippetPermissionRequest.snippetId}",
            )
        }
        validatePermissionExists(snippetPermissionRequest.snippetId, targetUserId)
        return snippetPermissionsRepository.revokeSnippetAccess(snippetPermissionRequest.snippetId, targetUserId)
    }

    private fun resolveUserId(
        snippetPermissionRequest: SnippetPermissionRequest,
        requestingUserId: String,
    ): String = snippetPermissionRequest.userId ?: requestingUserId

    private fun validateUserCanGrantPermission(
        snippetId: String,
        userId: String,
    ) {
        val userPermission = snippetPermissionsRepository.findBySnippetIdAndUserId(snippetId, userId)
        if (userPermission == null) {
            val anyPermissionForSnippet = snippetPermissionsRepository.findBySnippetId(snippetId)
            if (anyPermissionForSnippet.isEmpty()) {
                return
            }
            throw UnauthorizedPermissionActionException(
                "User $userId does not have WRITE permission to grant access to snippet: $snippetId",
            )
        }

        if (userPermission.permission != Permissions.WRITE) {
            throw UnauthorizedPermissionActionException(
                "User $userId does not have WRITE permission to grant access to snippet: $snippetId",
            )
        }
    }

    private fun validateUserCanRevokePermission(
        snippetId: String,
        userId: String,
    ) {
        val userPermission = snippetPermissionsRepository.findBySnippetIdAndUserId(snippetId, userId)

        if (userPermission == null || userPermission.permission != Permissions.WRITE) {
            throw UnauthorizedPermissionActionException(
                "User $userId does not have WRITE permission to revoke access to snippet: $snippetId",
            )
        }
    }

    private fun validatePermissionDoesNotExist(
        snippetId: String,
        userId: String,
    ) {
        val existingPermission = snippetPermissionsRepository.findBySnippetIdAndUserId(snippetId, userId)

        if (existingPermission != null) {
            throw PermissionAlreadyExistsException(
                "Permission already exists for user: $userId on snippet: $snippetId",
            )
        }
    }

    private fun validatePermissionExists(
        snippetId: String,
        userId: String,
    ) {
        snippetPermissionsRepository.findBySnippetIdAndUserId(snippetId, userId) ?: throw PermissionNotFoundException(
            "Permission not found for user: $userId on snippet: $snippetId",
        )
    }
}
