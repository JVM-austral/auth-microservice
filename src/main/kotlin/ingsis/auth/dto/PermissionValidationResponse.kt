package ingsis.auth.dto

data class PermissionValidationResponse(
    val allowed: Boolean,
    val reason: String? = null,
)
