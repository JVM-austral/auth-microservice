package ingsis.auth.service

import ingsis.auth.dto.SnippetPermissionRequest
import ingsis.auth.exception.UnauthorizedPermissionActionException
import ingsis.auth.repository.SnippetPermissionsRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SnippetPermissionValidateServiceTest {
    lateinit var service: SnippetPermissionValidateService
    lateinit var repository: SnippetPermissionsRepository

    @BeforeEach
    fun setup() {
        repository = mockk()
        service = SnippetPermissionValidateService(repository)
    }

    @Test
    fun `should allow write access when user has write permission`() {
        val request = SnippetPermissionRequest(snippetId = "snippet-1")
        val userId = "user-1"

        every { repository.userHasWriteAccess("snippet-1", userId) } returns true

        val result = service.validateSnippetWriteAccess(request, userId)

        assertTrue(result.allowed)
        verify(exactly = 1) { repository.userHasWriteAccess("snippet-1", userId) }
    }

    @Test
    fun `should throw exception when user does not have write access`() {
        val request = SnippetPermissionRequest(snippetId = "snippet-2")
        val userId = "user-2"

        every { repository.userHasWriteAccess("snippet-2", userId) } returns false

        val exception =
            assertThrows<UnauthorizedPermissionActionException> {
                service.validateSnippetWriteAccess(request, userId)
            }

        assertEquals(
            "User user-2 does not have WRITE permission to grant access to snippet: snippet-2",
            exception.message,
        )
        verify(exactly = 1) { repository.userHasWriteAccess("snippet-2", userId) }
    }

    @Test
    fun `should allow read access when user has read permission`() {
        val request = SnippetPermissionRequest(snippetId = "snippet-3")
        val userId = "user-3"

        every { repository.userHasReadAccess("snippet-3", userId) } returns true

        val result = service.validateSnippetReadAccess(request, userId)

        assertTrue(result.allowed)
        verify(exactly = 1) { repository.userHasReadAccess("snippet-3", userId) }
    }

    @Test
    fun `should allow read access when user has write permission`() {
        val request = SnippetPermissionRequest(snippetId = "snippet-4")
        val userId = "user-4"

        every { repository.userHasReadAccess("snippet-4", userId) } returns true

        val result = service.validateSnippetReadAccess(request, userId)

        assertTrue(result.allowed)
        verify(exactly = 1) { repository.userHasReadAccess("snippet-4", userId) }
    }

    @Test
    fun `should throw exception when user does not have read access`() {
        val request = SnippetPermissionRequest(snippetId = "snippet-5")
        val userId = "user-5"

        every { repository.userHasReadAccess("snippet-5", userId) } returns false

        val exception =
            assertThrows<UnauthorizedPermissionActionException> {
                service.validateSnippetReadAccess(request, userId)
            }

        assertEquals(
            "User user-5 has no read access to snippet snippet-5",
            exception.message,
        )
        verify(exactly = 1) { repository.userHasReadAccess("snippet-5", userId) }
    }

    @Test
    fun `should validate write access for specified userId in request`() {
        val targetUserId = "user-target"
        val requestingUserId = "user-requesting"
        val request = SnippetPermissionRequest(snippetId = "snippet-6", userId = targetUserId)

        every { repository.userHasWriteAccess("snippet-6", targetUserId) } returns true

        val result = service.validateSnippetWriteAccess(request, requestingUserId)

        assertTrue(result.allowed)
        verify(exactly = 1) { repository.userHasWriteAccess("snippet-6", targetUserId) }
    }

    @Test
    fun `should validate read access for specified userId in request`() {
        val targetUserId = "user-target"
        val requestingUserId = "user-requesting"
        val request = SnippetPermissionRequest(snippetId = "snippet-7", userId = targetUserId)

        every { repository.userHasReadAccess("snippet-7", targetUserId) } returns true

        val result = service.validateSnippetReadAccess(request, requestingUserId)

        assertTrue(result.allowed)
        verify(exactly = 1) { repository.userHasReadAccess("snippet-7", targetUserId) }
    }

    @Test
    fun `should use requesting userId when userId is not specified in request for write validation`() {
        val requestingUserId = "user-8"
        val request = SnippetPermissionRequest(snippetId = "snippet-8", userId = null)

        every { repository.userHasWriteAccess("snippet-8", requestingUserId) } returns true

        val result = service.validateSnippetWriteAccess(request, requestingUserId)

        assertTrue(result.allowed)
        verify(exactly = 1) { repository.userHasWriteAccess("snippet-8", requestingUserId) }
    }

    @Test
    fun `should use requesting userId when userId is not specified in request for read validation`() {
        val requestingUserId = "user-9"
        val request = SnippetPermissionRequest(snippetId = "snippet-9", userId = null)

        every { repository.userHasReadAccess("snippet-9", requestingUserId) } returns true

        val result = service.validateSnippetReadAccess(request, requestingUserId)

        assertTrue(result.allowed)
        verify(exactly = 1) { repository.userHasReadAccess("snippet-9", requestingUserId) }
    }
}
