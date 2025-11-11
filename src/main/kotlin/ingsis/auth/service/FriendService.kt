package ingsis.auth.service

import ingsis.auth.dto.UserSubjectDto
import ingsis.auth.entity.Friend
import ingsis.auth.repository.FriendsRepository
import org.springframework.stereotype.Service

@Service
class FriendService(
    private val friendsRepository: FriendsRepository,
) {
    private fun orderIds(
        id1: String,
        id2: String,
    ): Pair<String, String> = if (id1 < id2) Pair(id1, id2) else Pair(id2, id1)

    fun getAll(userId: String): List<UserSubjectDto> {
        val friendships = friendsRepository.findAllByUserId(userId)

        return friendships.map { friendship ->
            val friendId =
                if (friendship.userId == userId) {
                    friendship.friendId
                } else {
                    friendship.userId
                }
            UserSubjectDto(friendId)
        }
    }

    fun areFriends(
        userId: String,
        friendId: String,
    ): Boolean {
        val (smallerId, biggerId) = orderIds(userId, friendId)
        return friendsRepository.existsByUserIdAndFriendId(smallerId, biggerId)
    }

    fun addFriend(
        userId: String,
        friendId: String,
    ): Friend {
        if (userId.isBlank() || friendId.isBlank()) {
            throw ingsis.auth.exception.InvalidFriendIdException("The user ID and friend ID must not be blank.")
        }

        if (userId == friendId) {
            throw ingsis.auth.exception.SelfFriendshipNotAllowedException()
        }

        val (smallerId, biggerId) = orderIds(userId, friendId)

        if (friendsRepository.existsByUserIdAndFriendId(smallerId, biggerId)) {
            throw ingsis.auth.exception.FriendshipAlreadyExistsException()
        }

        return friendsRepository.save(Friend(userId = smallerId, friendId = biggerId))
    }

    fun removeFriend(
        userId: String,
        friendId: String,
    ) {
        val (smallerId, biggerId) = orderIds(userId, friendId)

        val friendship =
            friendsRepository.findByUserIdAndFriendId(smallerId, biggerId)
                ?: throw ingsis.auth.exception.FriendshipNotFoundException()

        friendsRepository.delete(friendship)
    }

    fun countFriends(userId: String): Long = friendsRepository.countFriendsByUserId(userId)
}
