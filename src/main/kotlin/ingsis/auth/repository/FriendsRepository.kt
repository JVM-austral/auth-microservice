package ingsis.auth.repository

import ingsis.auth.entity.Friend
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FriendsRepository : JpaRepository<Friend, String> {
    @Query(
        """
        SELECT f FROM Friend f 
        WHERE f.userId = :userId OR f.friendId = :userId
    """,
    )
    fun findAllByUserId(
        @Param("userId") userId: String,
    ): List<Friend>

    fun findByUserIdAndFriendId(
        userId: String,
        friendId: String,
    ): Friend?

    fun existsByUserIdAndFriendId(
        userId: String,
        friendId: String,
    ): Boolean

    @Query(
        """
        SELECT COUNT(f) FROM Friend f 
        WHERE f.userId = :userId OR f.friendId = :userId
    """,
    )
    fun countFriendsByUserId(
        @Param("userId") userId: String,
    ): Long
}
