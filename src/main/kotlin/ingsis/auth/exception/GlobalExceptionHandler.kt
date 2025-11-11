package ingsis.auth.exception

import ingsis.auth.dto.error.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(PermissionAlreadyExistsException::class)
    fun handlePermissionAlreadyExists(ex: PermissionAlreadyExistsException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ErrorResponse(ex.message ?: "Permission already exists"))

    @ExceptionHandler(UnauthorizedPermissionActionException::class)
    fun handleUnauthorizedAction(ex: UnauthorizedPermissionActionException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ErrorResponse(ex.message ?: "Unauthorized action"))

    @ExceptionHandler(PermissionNotFoundException::class)
    fun handlePermissionNotFound(ex: PermissionNotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(ex.message ?: "Permission not found"))

    @ExceptionHandler(SelfRevocationNotAllowedException::class)
    fun handleSelfRevocation(ex: SelfRevocationNotAllowedException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(ex.message ?: "Cannot revoke own permissions"))

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserAlreadyExists(ex: UserAlreadyExistsException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ErrorResponse(message = ex.message ?: "User already exists"))

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(ex: UserNotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                ErrorResponse(
                    message = ex.message ?: "User not found",
                ),
            )

    @ExceptionHandler(FriendshipAlreadyExistsException::class)
    fun handleFriendshipAlreadyExists(ex: FriendshipAlreadyExistsException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ErrorResponse(message = ex.message ?: "The friendship already exists"))

    @ExceptionHandler(FriendshipNotFoundException::class)
    fun handleFriendshipNotFound(ex: FriendshipNotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(message = ex.message ?: "The friendship was not found"))

    @ExceptionHandler(SelfFriendshipNotAllowedException::class)
    fun handleSelfFriendship(ex: SelfFriendshipNotAllowedException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(message = ex.message ?: "Cannot befriend oneself"))

    @ExceptionHandler(InvalidFriendIdException::class)
    fun handleInvalidFriendId(ex: InvalidFriendIdException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(message = ex.message ?: "Invalid friend ID"))
}
