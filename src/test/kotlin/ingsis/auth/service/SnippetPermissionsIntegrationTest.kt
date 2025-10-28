package ingsis.auth.service

import ingsis.auth.common.Permissions
import ingsis.auth.dto.SnippetPermissionRequest
import ingsis.auth.entity.SnippetPermissions
import ingsis.auth.exception.PermissionNotFoundException
import ingsis.auth.repository.SnippetPermissionsRepository
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
class SnippetPermissionsIntegrationTest(
    @Autowired val repository: SnippetPermissionsRepository,
    @Autowired val service: SnippetPermissionsService,
) {
    @Test
    fun `should revoke existing snippet permission from database`() {
        val owner =
            repository.grantSnippetWriteAccess(
                SnippetPermissions(
                    id = UUID.randomUUID().toString(),
                    snippetId = "snippet-100",
                    userId = "user-owner",
                    permission = Permissions.WRITE,
                ),
            )

        val saved =
            repository.grantSnippetWriteAccess(
                SnippetPermissions(
                    id = UUID.randomUUID().toString(),
                    snippetId = "snippet-100",
                    userId = "user-100",
                    permission = Permissions.WRITE,
                ),
            )

        val request = SnippetPermissionRequest(snippetId = saved.snippetId, userId = saved.userId)
        val result = service.revokeSnippetAccess(request, owner.userId)

        assertTrue(result)
        assertTrue(repository.findAll().none { it.id == saved.id })
    }

    @Test
    fun `should not revoke non-existent permission`() {
        repository.grantSnippetWriteAccess(
            SnippetPermissions(
                id = UUID.randomUUID().toString(),
                snippetId = "non-existent",
                userId = "user-owner",
                permission = Permissions.WRITE,
            ),
        )

        val request = SnippetPermissionRequest(snippetId = "non-existent", userId = "user-404")
        assertThrows<PermissionNotFoundException> {
            service.revokeSnippetAccess(request, "user-owner")
        }
    }
}
