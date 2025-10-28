package ingsis.auth.service

import ingsis.auth.common.Permissions
import ingsis.auth.dto.SnippetPermissionRequest
import ingsis.auth.entity.SnippetPermissions
import ingsis.auth.exception.PermissionNotFoundException
import ingsis.auth.repository.SnippetPermissionsRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class SnippetPermissionsServiceTest {
    private lateinit var repository: SnippetPermissionsRepository
    private lateinit var service: SnippetPermissionsService

    @BeforeEach
    fun setup() {
        repository = mockk()
        service = SnippetPermissionsService(repository)
    }

    @Test
    fun `should grant write access`() {
        val request = SnippetPermissionRequest(snippetId = "snippet-1")
        val userId = "user-1"

        every { repository.findBySnippetIdAndUserId(request.snippetId, userId) } returns null
        every { repository.findBySnippetId(request.snippetId) } returns emptyList()

        val expectedPermission =
            SnippetPermissions(
                id = UUID.randomUUID().toString(),
                snippetId = request.snippetId,
                userId = userId,
                permission = Permissions.WRITE,
            )
        every { repository.grantSnippetWriteAccess(any()) } returns expectedPermission

        val result = service.grantSnippetWriteAccess(request, userId)

        assertEquals(Permissions.WRITE, result.permission)
        assertEquals(request.snippetId, result.snippetId)
        assertEquals(userId, result.userId)

        verify(exactly = 1) { repository.grantSnippetWriteAccess(any()) }
    }

    @Test
    fun `should grant read access`() {
        val request = SnippetPermissionRequest(snippetId = "snippet-2", userId = "user-1")
        val userId = "user-2"

        val userPermission =
            SnippetPermissions(
                id = UUID.randomUUID().toString(),
                snippetId = request.snippetId,
                userId = userId,
                permission = Permissions.WRITE,
            )
        every { repository.findBySnippetIdAndUserId(request.snippetId, userId) } returns userPermission

        every { repository.findBySnippetIdAndUserId(request.snippetId, "user-1") } returns null

        val expectedPermission =
            SnippetPermissions(
                id = UUID.randomUUID().toString(),
                snippetId = request.snippetId,
                userId = "user-1",
                permission = Permissions.READ,
            )
        every { repository.grantSnippetReadAccess(any()) } returns expectedPermission

        val result = service.grantSnippetReadAccess(request, userId)

        assertEquals(Permissions.READ, result.permission)
        assertEquals(request.snippetId, result.snippetId)
        assertEquals("user-1", result.userId)

        verify(exactly = 1) { repository.grantSnippetReadAccess(any()) }
    }

    @Test
    fun `should revoke snippet access when it exists`() {
        val requestingUserId = "user-owner"
        val targetUserId = "user-1"
        val request = SnippetPermissionRequest(snippetId = "snippet-3", userId = targetUserId)

        val ownerPermission =
            SnippetPermissions(
                id = UUID.randomUUID().toString(),
                snippetId = request.snippetId,
                userId = requestingUserId,
                permission = Permissions.WRITE,
            )
        every { repository.findBySnippetIdAndUserId(request.snippetId, requestingUserId) } returns ownerPermission

        val targetPermission =
            SnippetPermissions(
                id = UUID.randomUUID().toString(),
                snippetId = request.snippetId,
                userId = targetUserId,
                permission = Permissions.READ,
            )
        every { repository.findBySnippetIdAndUserId(request.snippetId, targetUserId) } returns targetPermission

        every { repository.revokeSnippetAccess(request.snippetId, targetUserId) } returns true

        val result = service.revokeSnippetAccess(request, requestingUserId)

        assertTrue(result)
        verify(exactly = 1) { repository.revokeSnippetAccess(request.snippetId, targetUserId) }
    }

    @Test
    fun `should return false when trying to revoke non-existing access`() {
        val requestingUserId = "user-owner"
        val targetUserId = "user-4"
        val request = SnippetPermissionRequest(snippetId = "snippet-4", userId = targetUserId)

        val ownerPermission =
            SnippetPermissions(
                id = UUID.randomUUID().toString(),
                snippetId = request.snippetId,
                userId = requestingUserId,
                permission = Permissions.WRITE,
            )
        every { repository.findBySnippetIdAndUserId(request.snippetId, requestingUserId) } returns ownerPermission
        every { repository.findBySnippetIdAndUserId(request.snippetId, targetUserId) } returns null

        assertThrows<PermissionNotFoundException> {
            service.revokeSnippetAccess(request, requestingUserId)
        }
    }
}
