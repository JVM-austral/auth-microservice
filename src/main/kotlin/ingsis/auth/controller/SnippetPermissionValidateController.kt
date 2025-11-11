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
    private val log = org.slf4j.LoggerFactory.getLogger(SnippetPermissionValidateController::class.java)
    @PostMapping("/validate-write")
    fun validateWriteAccess(
        @RequestBody request: SnippetPermissionRequest,
        @AuthenticationPrincipal jwt: Jwt,
    ): PermissionValidationResponse {
        log.info("Validating write access for user: ${jwt.subject} on snippet: ${request.snippetId}")
        return validateService.validateSnippetWriteAccess(request, jwt.subject)
    }

    @PostMapping("/validate-read-access")
    fun validateSnippetReadAccess(
        @AuthenticationPrincipal jwt: Jwt,
        @Valid @RequestBody snippetPermissionRequest: SnippetPermissionRequest,
    ): PermissionValidationResponse {
        log.info("Validating read access for user: ${jwt.subject} on snippet: ${snippetPermissionRequest.snippetId}")
        return validateService.validateSnippetReadAccess(snippetPermissionRequest, jwt.subject)
    }
}
