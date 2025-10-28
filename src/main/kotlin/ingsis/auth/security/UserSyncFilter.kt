package ingsis.auth.security

import ingsis.auth.entity.User
import ingsis.auth.repository.UserRepository
import ingsis.auth.service.UserService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class UserSyncFilter(
    private val userRepository: UserRepository,
    private val userService: UserService,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication?.principal is Jwt) {
            val jwt = authentication.principal as Jwt
            val userId = jwt.subject
            val name = jwt.getClaim<String>("name")

            if (!userRepository.existsById(userId)) {
                userService.save(User(id = userId, name = name ?: "juanito"))
            }
        }

        filterChain.doFilter(request, response)
    }
}
