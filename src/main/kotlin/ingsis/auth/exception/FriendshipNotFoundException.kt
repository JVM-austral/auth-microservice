package ingsis.auth.exception

class FriendshipNotFoundException(
    message: String = "The friendship does not exist",
) : RuntimeException(message)
