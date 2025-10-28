package ingsis.auth.controller

import ingsis.auth.dto.PermissionValidationResponse
import ingsis.auth.dto.SnippetPermissionRequest
import ingsis.auth.service.SnippetPermissionValidateService
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/snippet-permissions")
class SnippetPermissionValidateController(
    private val validateService: SnippetPermissionValidateService,
) {
    @PostMapping("/validate-write")
    fun validateWriteAccess(
        @RequestBody request: SnippetPermissionRequest,
        @AuthenticationPrincipal jwt: Jwt,
    ): PermissionValidationResponse = validateService.validateSnippetWriteAccess(request, jwt.subject)

    @PostMapping("/validate-read-access")
    fun validateSnippetReadAccess(
        @AuthenticationPrincipal jwt: Jwt,
        @Valid @RequestBody snippetPermissionRequest: SnippetPermissionRequest,
    ): PermissionValidationResponse = validateService.validateSnippetReadAccess(snippetPermissionRequest, jwt.subject)
}
