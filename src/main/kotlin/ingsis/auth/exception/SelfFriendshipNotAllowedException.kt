package ingsis.auth.exception

class SelfFriendshipNotAllowedException(
    message: String = "Not allowed to befriend oneself",
) : RuntimeException(message)
