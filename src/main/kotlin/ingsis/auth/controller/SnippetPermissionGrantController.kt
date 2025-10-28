package ingsis.auth.controller

import ingsis.auth.dto.SnippetPermissionRequest
import ingsis.auth.entity.SnippetPermissions
import ingsis.auth.service.SnippetPermissionGrantService
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/snippet-permissions")
class SnippetPermissionGrantController(
    private val snippetPermissionGrantService: SnippetPermissionGrantService,
) {
    @PostMapping("/grant-write-access")
    fun grantSnippetWriteAccess(
        @Valid @RequestBody snippetPermissionRequest: SnippetPermissionRequest,
        @AuthenticationPrincipal jwt: Jwt,
    ): SnippetPermissions = snippetPermissionGrantService.grantSnippetWriteAccess(snippetPermissionRequest, jwt.subject)

    @PostMapping("/grant-read-access")
    fun grantSnippetReadAccess(
        @Valid @RequestBody snippetPermissionRequest: SnippetPermissionRequest,
        @AuthenticationPrincipal jwt: Jwt,
    ): SnippetPermissions = snippetPermissionGrantService.grantSnippetReadAccess(snippetPermissionRequest, jwt.subject)

    @DeleteMapping("/revoke-access")
    fun revokeSnippetAccess(
        @Valid @RequestBody snippetPermissionRequest: SnippetPermissionRequest,
        @AuthenticationPrincipal jwt: Jwt,
    ): Boolean = snippetPermissionGrantService.revokeSnippetAccess(snippetPermissionRequest, jwt.subject)
}
