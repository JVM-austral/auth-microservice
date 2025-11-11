package ingsis.auth.exception

class FriendshipAlreadyExistsException(
    message: String = "There is already a friendship between these users",
) : RuntimeException(message)
