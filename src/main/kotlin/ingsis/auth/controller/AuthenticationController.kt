package ingsis.auth.controller

import ingsis.auth.dto.UserSubjectDto
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/authentication")
class AuthenticationController {
    private val log = org.slf4j.LoggerFactory.getLogger(AuthenticationController::class.java)

    @GetMapping("/validate-user")
    fun validateUser(
        @AuthenticationPrincipal jwt: Jwt,
    ): UserSubjectDto {
        log.info("Validating user with subject: ${jwt.subject}")
        return UserSubjectDto(jwt.subject)
    }
}
