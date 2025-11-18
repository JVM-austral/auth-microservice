package ingsis.auth.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import ingsis.auth.controller.UserController
import ingsis.auth.dto.PaginatedUserResponse
import ingsis.auth.dto.UserForResponse
import ingsis.auth.entity.User
import ingsis.auth.service.UserService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class UserControllerTest {
    private lateinit var mockMvc: MockMvc
    private lateinit var userService: UserService
    private lateinit var controller: UserController
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        userService = mockk()
        controller = UserController(userService)
        objectMapper = ObjectMapper()

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    @Test
    fun `getAll should return list of all users`() {
        val users =
            listOf(
                User(id = "user1"),
                User(id = "user2"),
            )

        every { userService.findAll() } returns users

        mockMvc
            .perform(get("/users"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value("user1"))
            .andExpect(jsonPath("$[1].id").value("user2"))

        verify(exactly = 1) { userService.findAll() }
    }

    @Test
    fun `getPaginated should return paginated users with default parameters`() {
        val userForResponse =
            listOf(
                UserForResponse(id = "user1", email = "user1@example.com"),
                UserForResponse(id = "user2", email = "user2@example.com"),
            )

        val paginatedResponse =
            PaginatedUserResponse(
                users = userForResponse,
                count = 2,
                page = 0,
                pageSize = 10,
            )

        every { userService.findPaginatedUserWithNameFilter(null, 0, 10) } returns paginatedResponse

        mockMvc
            .perform(get("/users/paginated"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.users[0].id").value("user1"))
            .andExpect(jsonPath("$.users[0].email").value("user1@example.com"))
            .andExpect(jsonPath("$.count").value(2))
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.pageSize").value(10))

        verify(exactly = 1) { userService.findPaginatedUserWithNameFilter(null, 0, 10) }
    }

    @Test
    fun `getPaginated should return paginated users with custom parameters and filter`() {
        val userForResponse =
            listOf(
                UserForResponse(id = "user1", email = "john@example.com"),
            )

        val paginatedResponse =
            PaginatedUserResponse(
                users = userForResponse,
                count = 1,
                page = 1,
                pageSize = 5,
            )

        every { userService.findPaginatedUserWithNameFilter("john", 1, 5) } returns paginatedResponse

        mockMvc
            .perform(
                get("/users/paginated")
                    .param("page", "1")
                    .param("page_size", "5")
                    .param("filter", "john"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.users[0].id").value("user1"))
            .andExpect(jsonPath("$.count").value(1))
            .andExpect(jsonPath("$.page").value(1))
            .andExpect(jsonPath("$.pageSize").value(5))

        verify(exactly = 1) { userService.findPaginatedUserWithNameFilter("john", 1, 5) }
    }

    @Test
    fun `createUser should create and return new user`() {
        val newUser = User(id = "user3")

        every { userService.save(any()) } returns newUser

        mockMvc
            .perform(
                post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newUser)),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("user3"))

        verify(exactly = 1) { userService.save(any()) }
    }

    @Test
    fun `getUserById should return user by id`() {
        val user = User(id = "user1")

        every { userService.findById("user1") } returns user

        mockMvc
            .perform(get("/users/user1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("user1"))

        verify(exactly = 1) { userService.findById("user1") }
    }
}
