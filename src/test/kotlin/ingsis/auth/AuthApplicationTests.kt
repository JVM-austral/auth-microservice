package ingsis.auth

import ingsis.auth.security.AuthenticationServiceApplication
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(classes = [AuthenticationServiceApplication::class])
@ActiveProfiles("test")
class AuthApplicationTests {
    @Test
    fun contextLoads() {
    }
}
