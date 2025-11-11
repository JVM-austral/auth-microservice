package ingsis.auth.service

import ingsis.auth.dto.UserSubjectDto
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class FriendServiceTest {
    private lateinit var repository: FriendsRepository
    private lateinit var service: FriendService

    @BeforeEach
    fun setup() {
        repository = mockk()
        service = FriendService(repository)
    }

    @Test
    fun `should get all friends for a user`() {
        val userId = "user-1"
        val friends =
            listOf(
                Friend(id = UUID.randomUUID().toString(), userId = "user-1", friendId = "user-2"),
                Friend(id = UUID.randomUUID().toString(), userId = "user-1", friendId = "user-3"),
                Friend(id = UUID.randomUUID().toString(), userId = "user-0", friendId = "user-1"),
            )

        every { repository.findAllByUserId(userId) } returns friends

        val result = service.getAll(userId)

        assertEquals(3, result.size)
        assertTrue(result.contains(UserSubjectDto("user-2")))
        assertTrue(result.contains(UserSubjectDto("user-3")))
        assertTrue(result.contains(UserSubjectDto("user-0")))

        verify(exactly = 1) { repository.findAllByUserId(userId) }
    }

    @Test
    fun `should return empty list when user has no friends`() {
        val userId = "user-lonely"

        every { repository.findAllByUserId(userId) } returns emptyList()

        val result = service.getAll(userId)

        assertEquals(0, result.size)
        verify(exactly = 1) { repository.findAllByUserId(userId) }
    }

    @Test
    fun `should verify if two users are friends`() {
        val userId = "user-1"
        val friendId = "user-2"

        every { repository.existsByUserIdAndFriendId("user-1", "user-2") } returns true

        val result = service.areFriends(userId, friendId)

        assertTrue(result)
        verify(exactly = 1) { repository.existsByUserIdAndFriendId("user-1", "user-2") }
    }

    @Test
    fun `should verify if two users are not friends`() {
        val userId = "user-1"
        val friendId = "user-3"

        every { repository.existsByUserIdAndFriendId("user-1", "user-3") } returns false

        val result = service.areFriends(userId, friendId)

        assertFalse(result)
        verify(exactly = 1) { repository.existsByUserIdAndFriendId("user-1", "user-3") }
    }

    @Test
    fun `should add friend with ordered IDs`() {
        val userId = "user-2"
        val friendId = "user-1"

        every { repository.existsByUserIdAndFriendId("user-1", "user-2") } returns false
        every { repository.save(any()) } answers { firstArg() }

        val result = service.addFriend(userId, friendId)

        assertEquals("user-1", result.userId)
        assertEquals("user-2", result.friendId)

        verify(exactly = 1) { repository.existsByUserIdAndFriendId("user-1", "user-2") }
        verify(exactly = 1) { repository.save(any()) }
    }

    @Test
    fun `should throw exception when user tries to be friend with themselves`() {
        val userId = "user-1"

        assertThrows<SelfFriendshipNotAllowedException> {
            service.addFriend(userId, userId)
        }

        verify(exactly = 0) { repository.save(any()) }
    }

    @Test
    fun `should throw exception when friendship already exists`() {
        val userId = "user-1"
        val friendId = "user-2"

        every { repository.existsByUserIdAndFriendId("user-1", "user-2") } returns true

        assertThrows<FriendshipAlreadyExistsException> {
            service.addFriend(userId, friendId)
        }

        verify(exactly = 1) { repository.existsByUserIdAndFriendId("user-1", "user-2") }
        verify(exactly = 0) { repository.save(any()) }
    }

    @Test
    fun `should throw exception when userId is blank`() {
        assertThrows<InvalidFriendIdException> {
            service.addFriend("", "user-2")
        }
    }

    @Test
    fun `should throw exception when friendId is blank`() {
        assertThrows<InvalidFriendIdException> {
            service.addFriend("user-1", "")
        }
    }

    @Test
    fun `should count friends correctly`() {
        val userId = "user-1"

        every { repository.countFriendsByUserId(userId) } returns 5L

        val result = service.countFriends(userId)

        assertEquals(5L, result)
        verify(exactly = 1) { repository.countFriendsByUserId(userId) }
    }

    @Test
    fun `should handle ordering when adding friends with reverse order`() {
        val userId = "user-z"
        val friendId = "user-a"

        every { repository.existsByUserIdAndFriendId("user-a", "user-z") } returns false
        every { repository.save(any()) } answers { firstArg() }

        val result = service.addFriend(userId, friendId)

        assertEquals("user-a", result.userId)
        assertEquals("user-z", result.friendId)
    }
}
