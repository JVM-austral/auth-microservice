package ingsis.auth.exception

import ingsis.auth.dto.error.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(PermissionAlreadyExistsException::class)
    fun handlePermissionAlreadyExists(ex: PermissionAlreadyExistsException): ResponseEntity<ErrorResponse> {
        log.warn("Permission already exists: ${ex.message}")
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ErrorResponse(ex.message ?: "Permission already exists"))
    }

    @ExceptionHandler(UnauthorizedPermissionActionException::class)
    fun handleUnauthorizedAction(ex: UnauthorizedPermissionActionException): ResponseEntity<ErrorResponse> {
        log.warn("Unauthorized action attempted: ${ex.message}")
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ErrorResponse(ex.message ?: "Unauthorized action"))
    }

    @ExceptionHandler(PermissionNotFoundException::class)
    fun handlePermissionNotFound(ex: PermissionNotFoundException): ResponseEntity<ErrorResponse> {
        log.warn("Permission not found: ${ex.message}")
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(ex.message ?: "Permission not found"))
    }

    @ExceptionHandler(SelfRevocationNotAllowedException::class)
    fun handleSelfRevocation(ex: SelfRevocationNotAllowedException): ResponseEntity<ErrorResponse> {
        log.warn("Self-revocation attempted: ${ex.message}")
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(ex.message ?: "Cannot revoke own permissions"))
    }

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserAlreadyExists(ex: UserAlreadyExistsException): ResponseEntity<ErrorResponse> {
        log.warn("User already exists: ${ex.message}")
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ErrorResponse(message = ex.message ?: "User already exists"))
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(ex: UserNotFoundException): ResponseEntity<ErrorResponse> {
        log.warn("User not found: ${ex.message}")
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                ErrorResponse(
                    message = ex.message ?: "User not found",
                ),
            )
    }
}