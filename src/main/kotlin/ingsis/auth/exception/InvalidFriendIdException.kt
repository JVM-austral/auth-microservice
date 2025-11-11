package ingsis.auth.exception

class InvalidFriendIdException(
    message: String = "The provided friend ID is invalid",
) : RuntimeException(message)
