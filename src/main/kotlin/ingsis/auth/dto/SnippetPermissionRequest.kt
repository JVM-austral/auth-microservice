package ingsis.auth.dto

import jakarta.validation.constraints.NotBlank

data class SnippetPermissionRequest(
    @field:NotBlank(message = "snippetId is required")
    val snippetId: String,
    val userId: String? = null,
)
