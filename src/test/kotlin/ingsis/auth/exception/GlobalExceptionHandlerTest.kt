package ingsis.auth.exception

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class GlobalExceptionHandlerTest {
    private lateinit var handler: GlobalExceptionHandler

    @BeforeEach
    fun setUp() {
        handler = GlobalExceptionHandler()
    }

    @Test
    fun `handlePermissionAlreadyExists should return CONFLICT status with error message`() {
        val errorMessage = "Permission already exists for this resource"
        val exception = PermissionAlreadyExistsException(errorMessage)

        val response = handler.handlePermissionAlreadyExists(exception)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(errorMessage, response.body?.message)
    }

    @Test
    fun `handleUnauthorizedAction should return FORBIDDEN status with error message`() {
        val errorMessage = "User does not have permission to perform this action"
        val exception = UnauthorizedPermissionActionException(errorMessage)

        val response = handler.handleUnauthorizedAction(exception)

        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        assertEquals(errorMessage, response.body?.message)
    }

    @Test
    fun `handlePermissionNotFound should return NOT_FOUND status with error message`() {
        val errorMessage = "Permission with id 123 not found"
        val exception = PermissionNotFoundException(errorMessage)

        val response = handler.handlePermissionNotFound(exception)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(errorMessage, response.body?.message)
    }

    @Test
    fun `handleSelfRevocation should return BAD_REQUEST status with error message`() {
        val errorMessage = "Cannot revoke your own permissions"
        val exception = SelfRevocationNotAllowedException(errorMessage)

        val response = handler.handleSelfRevocation(exception)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(errorMessage, response.body?.message)
    }

    @Test
    fun `handleUserAlreadyExists should return CONFLICT status with error message`() {
        val errorMessage = "User with email test@example.com already exists"
        val exception = UserAlreadyExistsException(errorMessage)

        val response = handler.handleUserAlreadyExists(exception)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(errorMessage, response.body?.message)
    }

    @Test
    fun `handleUserNotFound should return NOT_FOUND status with error message`() {
        val errorMessage = "User with id 456 not found"
        val exception = UserNotFoundException(errorMessage)

        val response = handler.handleUserNotFound(exception)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(errorMessage, response.body?.message)
    }
}
