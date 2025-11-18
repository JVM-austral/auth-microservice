package ingsis.auth.service

import ingsis.auth.dto.PaginatedUserResponse
import ingsis.auth.entity.User
import ingsis.auth.exception.UserAlreadyExistsException
import ingsis.auth.exception.UserNotFoundException
import ingsis.auth.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UserServiceTest {
    private val userRepository = mockk<UserRepository>()
    private val oAuthService = mockk<OAuthService>()
    private val userService = UserService(userRepository, oAuthService)

    private val user1 = User(id = "1")
    private val user2 = User(id = "2")
    private val users = listOf(user1, user2)

    @Test
    fun `findAll should return all users`() {
        every { userRepository.findAll() } returns users
        every { oAuthService.getUserEmail(any()) } returns "email@test.com"

        val result = userService.findAll()
        assertEquals(users, result)
        verify(exactly = 1) { userRepository.findAll() }
    }

    @Test
    fun `findById should return user if exists`() {
        every { userRepository.findById("1") } returns java.util.Optional.of(user1)

        val result = userService.findById("1")
        assertEquals(user1, result)
        verify(exactly = 1) { userRepository.findById("1") }
    }

    @Test
    fun `findById should throw exception if user not found`() {
        every { userRepository.findById("3") } returns java.util.Optional.empty()

        assertFailsWith<UserNotFoundException> {
            userService.findById("3")
        }
    }

    @Test
    fun `save should save user if not exists`() {
        every { userRepository.existsById("1") } returns false
        every { userRepository.save(user1) } returns user1

        val result = userService.save(user1)
        assertEquals(user1, result)
        verify(exactly = 1) { userRepository.save(user1) }
    }

    @Test
    fun `save should throw exception if user already exists`() {
        every { userRepository.existsById("1") } returns true

        assertFailsWith<UserAlreadyExistsException> {
            userService.save(user1)
        }
    }

    @Test
    fun `findPaginatedUserWithNameFilter should return paginated users`() {
        every { userRepository.findAll() } returns users
        every { oAuthService.getUserEmail("1") } returns "email1@test.com"
        every { oAuthService.getUserEmail("2") } returns "email2@test.com"

        val result: PaginatedUserResponse =
            userService.findPaginatedUserWithNameFilter(
                filter = null,
                page = 0,
                pageSize = 1,
            )

        assertEquals(1, result.users.size)
        assertEquals(2, result.count)
        assertEquals(0, result.page)
        assertEquals(1, result.pageSize)
        assertEquals("1", result.users[0].id)
        assertEquals("email1@test.com", result.users[0].email)
    }
}
