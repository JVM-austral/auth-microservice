package ingsis.auth.controller

import ingsis.auth.entity.SnippetPermissions
import ingsis.auth.service.SnippetPermissionsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/snippet-permissions")
class SnippetPermissionsController(
    private val snippetPermissionsService: SnippetPermissionsService,
) {
    @PostMapping
    fun createSnippetPermission(
        @RequestParam snippetId: String,
        @RequestParam userId: String,
        @RequestParam permission: String,
    ): SnippetPermissions = snippetPermissionsService.createSnippetPermission(snippetId, userId, permission)

    @GetMapping("/by-snippet/{snippetId}")
    fun getBySnippet(
        @PathVariable snippetId: String,
    ) =
        snippetPermissionsService.getBySnippet(snippetId)

    @GetMapping("/by-user/{userId}")
    fun getByUser(
        @PathVariable userId: String,
    ) =
        snippetPermissionsService.getByUser(userId)
}
