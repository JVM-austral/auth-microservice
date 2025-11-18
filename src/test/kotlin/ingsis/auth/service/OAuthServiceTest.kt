package ingsis.auth.service

import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockConstruction
import org.mockito.Mockito.`when`
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.test.assertEquals

class OAuthServiceTest {
    @Test
    fun `getUserEmail should return email from Auth0`() {
        val connToken = mock(HttpURLConnection::class.java)
        val connUser = mock(HttpURLConnection::class.java)

        `when`(connToken.outputStream).thenReturn(mock(OutputStream::class.java))
        `when`(connToken.responseCode).thenReturn(200)
        `when`(connToken.inputStream).thenReturn("""{"access_token":"TOKEN123"}""".byteInputStream())

        `when`(connUser.responseCode).thenReturn(200)
        `when`(connUser.inputStream).thenReturn("""{"email":"vito@test.com"}""".byteInputStream())

        var callCount = 0

        val construction =
            mockConstruction(URL::class.java) { urlMock, _ ->
                `when`(urlMock.openConnection()).thenAnswer {
                    if (callCount++ == 0) connToken else connUser
                }
            }

        try {
            val service =
                OAuthService(
                    domain = "https://testdomain.com",
                    clientId = "abc",
                    clientSecret = "123",
                    managementApiAudience = "aud2",
                )

            val email = service.getUserEmail("123")
            assertEquals("vito@test.com", email)
        } finally {
            construction.close()
        }
    }

    @Test
    fun `getUserEmail should return mail_not_found if email field missing`() {
        val connToken = mock(HttpURLConnection::class.java)
        val connUser = mock(HttpURLConnection::class.java)

        `when`(connToken.outputStream).thenReturn(mock(OutputStream::class.java))
        `when`(connToken.responseCode).thenReturn(200)
        `when`(connToken.inputStream).thenReturn("""{"access_token":"TOKEN123"}""".byteInputStream())

        `when`(connUser.responseCode).thenReturn(200)
        `when`(connUser.inputStream).thenReturn("""{"name":"Vito"}""".byteInputStream())

        var callCount = 0

        val construction =
            mockConstruction(URL::class.java) { urlMock, _ ->
                `when`(urlMock.openConnection()).thenAnswer {
                    if (callCount++ == 0) connToken else connUser
                }
            }

        try {
            val service =
                OAuthService(
                    domain = "https://testdomain.com",
                    clientId = "abc",
                    clientSecret = "123",
                    managementApiAudience = "aud2",
                )

            val email = service.getUserEmail("123")
            assertEquals("mail_not_found", email)
        } finally {
            construction.close()
        }
    }

    @Test
    fun `getUserEmail should throw exception on token error`() {
        val connToken = mock(HttpURLConnection::class.java)

        `when`(connToken.outputStream).thenReturn(mock(OutputStream::class.java))
        `when`(connToken.responseCode).thenReturn(500)

        val construction =
            mockConstruction(URL::class.java) { urlMock, _ ->
                `when`(urlMock.openConnection()).thenReturn(connToken)
            }

        try {
            val service =
                OAuthService(
                    domain = "https://testdomain.com",
                    clientId = "abc",
                    clientSecret = "123",
                    managementApiAudience = "aud2",
                )

            try {
                service.getUserEmail("123")
                assert(false) { "Exception was expected" }
            } catch (ex: Exception) {
                assert(ex.message!!.contains("Error al obtener token"))
            }
        } finally {
            construction.close()
        }
    }
}
