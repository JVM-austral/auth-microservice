package ingsis.auth.dto

data class PaginatedUserResponse(
    val users: List<UserForResponse>,
    val count: Int,
    val page: Int,
    val pageSize: Int,
)
