package ingsis.auth.integration

import ingsis.auth.common.Permissions
import ingsis.auth.dto.SnippetPermissionRequest
import ingsis.auth.entity.SnippetPermissions
import ingsis.auth.exception.UnauthorizedPermissionActionException
import ingsis.auth.repository.SnippetPermissionsRepository
import ingsis.auth.service.SnippetPermissionValidateService
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.UUID

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
class SnippetPermissionValidateIntegrationTest(
    @Autowired val repository: SnippetPermissionsRepository,
    @Autowired val validateService: SnippetPermissionValidateService,
) {
    @Test
    fun `should validate write access for user with write permission`() {
        val permission =
            repository.grantSnippetWriteAccess(
                SnippetPermissions(
                    id = UUID.randomUUID().toString(),
                    snippetId = "snippet-write-1",
                    userId = "user-write-1",
                    permission = Permissions.WRITE,
                ),
            )

        val request = SnippetPermissionRequest(snippetId = permission.snippetId, userId = permission.userId)
        val result = validateService.validateSnippetWriteAccess(request, permission.userId)

        assertTrue(result.allowed)
    }

    @Test
    fun `should throw exception when validating write access for user without permission`() {
        repository.grantSnippetWriteAccess(
            SnippetPermissions(
                id = UUID.randomUUID().toString(),
                snippetId = "snippet-write-2",
                userId = "user-owner-2",
                permission = Permissions.WRITE,
            ),
        )

        val request = SnippetPermissionRequest(snippetId = "snippet-write-2", userId = "user-no-access")

        assertThrows<UnauthorizedPermissionActionException> {
            validateService.validateSnippetWriteAccess(request, "user-owner-2")
        }
    }

    @Test
    fun `should throw exception when validating write access for user with only read permission`() {
        val readPermission =
            repository.grantSnippetReadAccess(
                SnippetPermissions(
                    id = UUID.randomUUID().toString(),
                    snippetId = "snippet-write-3",
                    userId = "user-read-only",
                    permission = Permissions.READ,
                ),
            )

        val request =
            SnippetPermissionRequest(
                snippetId = readPermission.snippetId,
                userId = readPermission.userId,
            )

        assertThrows<UnauthorizedPermissionActionException> {
            validateService.validateSnippetWriteAccess(request, readPermission.userId)
        }
    }

    @Test
    fun `should validate read access for user with read permission`() {
        val permission =
            repository.grantSnippetReadAccess(
                SnippetPermissions(
                    id = UUID.randomUUID().toString(),
                    snippetId = "snippet-read-1",
                    userId = "user-read-1",
                    permission = Permissions.READ,
                ),
            )

        val request = SnippetPermissionRequest(snippetId = permission.snippetId, userId = permission.userId)
        val result = validateService.validateSnippetReadAccess(request, permission.userId)

        assertTrue(result.allowed)
    }

    @Test
    fun `should validate read access for user with write permission`() {
        val permission =
            repository.grantSnippetWriteAccess(
                SnippetPermissions(
                    id = UUID.randomUUID().toString(),
                    snippetId = "snippet-read-2",
                    userId = "user-write-2",
                    permission = Permissions.WRITE,
                ),
            )

        val request = SnippetPermissionRequest(snippetId = permission.snippetId, userId = permission.userId)
        val result = validateService.validateSnippetReadAccess(request, permission.userId)

        assertTrue(result.allowed)
    }

    @Test
    fun `should throw exception when validating read access for user without permission`() {
        repository.grantSnippetWriteAccess(
            SnippetPermissions(
                id = UUID.randomUUID().toString(),
                snippetId = "snippet-read-3",
                userId = "user-owner-3",
                permission = Permissions.WRITE,
            ),
        )

        val request = SnippetPermissionRequest(snippetId = "snippet-read-3", userId = "user-no-read")

        assertThrows<UnauthorizedPermissionActionException> {
            validateService.validateSnippetReadAccess(request, "user-owner-3")
        }
    }

    @Test
    fun `should validate access using requesting user when userId is not specified`() {
        val permission =
            repository.grantSnippetWriteAccess(
                SnippetPermissions(
                    id = UUID.randomUUID().toString(),
                    snippetId = "snippet-read-4",
                    userId = "user-requesting",
                    permission = Permissions.WRITE,
                ),
            )

        val request = SnippetPermissionRequest(snippetId = permission.snippetId, userId = null)
        val result = validateService.validateSnippetReadAccess(request, "user-requesting")

        assertTrue(result.allowed)
    }
}
