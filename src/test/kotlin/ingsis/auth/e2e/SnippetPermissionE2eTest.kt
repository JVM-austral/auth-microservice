package ingsis.auth.e2e

import com.fasterxml.jackson.databind.ObjectMapper
import ingsis.auth.common.Permissions
import ingsis.auth.dto.SnippetPermissionRequest
import ingsis.auth.entity.SnippetPermissions
import ingsis.auth.repository.SnippetPermissionsRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
class SnippetPermissionE2eTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var repository: SnippetPermissionsRepository

    @Test
    fun `e2e test - grant write access endpoint responds`() {
        val userId = "e2e-user-1"
        val request = SnippetPermissionRequest(snippetId = "e2e-snippet-1")

        mockMvc
            .perform(
                post("/snippet-permissions/grant-write-access")
                    .with(jwt().jwt { it.subject(userId) })
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.snippetId").value("e2e-snippet-1"))
            .andExpect(jsonPath("$.userId").value(userId))
            .andExpect(jsonPath("$.permission").value("WRITE"))
    }

    @Test
    fun `e2e test - grant read access endpoint responds`() {
        val ownerId = "e2e-owner-2"
        val targetUserId = "e2e-target-2"

        repository.grantSnippetWriteAccess(
            SnippetPermissions(
                id = UUID.randomUUID().toString(),
                snippetId = "e2e-snippet-2",
                userId = ownerId,
                permission = Permissions.WRITE,
            ),
        )

        val request =
            SnippetPermissionRequest(
                snippetId = "e2e-snippet-2",
                userId = targetUserId,
            )

        mockMvc
            .perform(
                post("/snippet-permissions/grant-read-access")
                    .with(jwt().jwt { it.subject(ownerId) })
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.snippetId").value("e2e-snippet-2"))
            .andExpect(jsonPath("$.userId").value(targetUserId))
            .andExpect(jsonPath("$.permission").value("READ"))
    }

    @Test
    fun `e2e test - validate write access endpoint responds`() {
        val userId = "e2e-user-3"

        repository.grantSnippetWriteAccess(
            SnippetPermissions(
                id = UUID.randomUUID().toString(),
                snippetId = "e2e-snippet-3",
                userId = userId,
                permission = Permissions.WRITE,
            ),
        )

        val request = SnippetPermissionRequest(snippetId = "e2e-snippet-3")

        mockMvc
            .perform(
                post("/snippet-permissions/validate-write")
                    .with(jwt().jwt { it.subject(userId) })
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.allowed").value(true))
    }

    @Test
    fun `e2e test - validate read access endpoint responds`() {
        val userId = "e2e-user-4"

        repository.grantSnippetReadAccess(
            SnippetPermissions(
                id = UUID.randomUUID().toString(),
                snippetId = "e2e-snippet-4",
                userId = userId,
                permission = Permissions.READ,
            ),
        )

        val request = SnippetPermissionRequest(snippetId = "e2e-snippet-4")

        mockMvc
            .perform(
                post("/snippet-permissions/validate-read-access")
                    .with(jwt().jwt { it.subject(userId) })
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.allowed").value(true))
    }

    @Test
    fun `e2e test - revoke access endpoint responds`() {
        val ownerId = "e2e-owner-5"
        val targetUserId = "e2e-target-5"

        repository.grantSnippetWriteAccess(
            SnippetPermissions(
                id = UUID.randomUUID().toString(),
                snippetId = "e2e-snippet-5",
                userId = ownerId,
                permission = Permissions.WRITE,
            ),
        )

        repository.grantSnippetReadAccess(
            SnippetPermissions(
                id = UUID.randomUUID().toString(),
                snippetId = "e2e-snippet-5",
                userId = targetUserId,
                permission = Permissions.READ,
            ),
        )

        val request =
            SnippetPermissionRequest(
                snippetId = "e2e-snippet-5",
                userId = targetUserId,
            )

        mockMvc
            .perform(
                delete("/snippet-permissions/revoke-access")
                    .with(jwt().jwt { it.subject(ownerId) })
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$").value(true))
    }

    @Test
    fun `e2e test - endpoints return 401 without authentication`() {
        val request = SnippetPermissionRequest(snippetId = "e2e-snippet-6")

        mockMvc
            .perform(
                post("/snippet-permissions/grant-write-access")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `e2e test - endpoints return 403 when user lacks permission`() {
        val unauthorizedUserId = "unauthorized-user"

        repository.grantSnippetWriteAccess(
            SnippetPermissions(
                id = UUID.randomUUID().toString(),
                snippetId = "e2e-snippet-7",
                userId = "different-owner",
                permission = Permissions.WRITE,
            ),
        )

        val request =
            SnippetPermissionRequest(
                snippetId = "e2e-snippet-7",
                userId = "some-target-user",
            )

        mockMvc
            .perform(
                post("/snippet-permissions/grant-read-access")
                    .with(jwt().jwt { it.subject(unauthorizedUserId) })
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isForbidden)
    }
}
