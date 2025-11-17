package ingsis.auth.repository

import ingsis.auth.entity.SnippetPermissions

interface SnippetPermissionsRepositoryInterface {
    fun grantSnippetWriteAccess(snippetPermissions: SnippetPermissions): SnippetPermissions

    fun grantSnippetReadAccess(snippetPermissions: SnippetPermissions): SnippetPermissions

    fun revokeSnippetAccess(
        snippetId: String,
        userId: String,
    ): Boolean

    fun findAll(): List<SnippetPermissions>

    fun findBySnippetIdAndUserId(
        snippetId: String,
        userId: String,
    ): SnippetPermissions?

    fun findBySnippetId(snippetId: String): List<SnippetPermissions>

    fun userHasWriteAccess(
        snippetId: String,
        userId: String,
    ): Boolean

    fun userHasReadAccess(
        snippetId: String,
        userId: String,
    ): Boolean

    fun getSharedSnippetIdsByUserId(userId: String): List<String>
}
