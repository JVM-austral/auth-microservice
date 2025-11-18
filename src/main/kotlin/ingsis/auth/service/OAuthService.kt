package ingsis.auth.service

import com.nimbusds.jose.shaded.gson.JsonObject
import com.nimbusds.jose.shaded.gson.JsonParser
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.HttpURLConnection
import java.net.URL

@Service
class OAuthService(
    @Value("\${auth0.domain}")
    private val domain: String,
    @Value("\${auth0.client-id}")
    private val clientId: String,
    @Value("\${auth0.client-secret}")
    private val clientSecret: String,
    @Value("\${auth0.management-api.audience}")
    private val managementApiAudience: String,
) {
    fun getUserEmail(id: String): String {
        val authDomain =
            if (domain.startsWith("http://") || domain.startsWith("https://")) {
                domain.trimEnd('/')
            } else {
                "https://${domain.trimEnd('/')}"
            }

        val tokenUrl = URL("$authDomain/oauth/token")
        val tokenConn = tokenUrl.openConnection() as HttpURLConnection

        tokenConn.requestMethod = "POST"
        tokenConn.setRequestProperty("Content-Type", "application/json")
        tokenConn.doOutput = true

        val body =
            JsonObject().apply {
                addProperty("client_id", clientId)
                addProperty("client_secret", clientSecret)
                addProperty("audience", managementApiAudience)
                addProperty("grant_type", "client_credentials")
            }

        tokenConn.outputStream.use { it.write(body.toString().toByteArray()) }

        if (tokenConn.responseCode != 200) {
            throw Exception("Error al obtener token (Management API): ${tokenConn.responseCode}")
        }

        val tokenResponse = tokenConn.inputStream.bufferedReader().use { it.readText() }
        val tokenJson = JsonParser.parseString(tokenResponse).asJsonObject
        val token = tokenJson["access_token"].asString

        val userUrl = URL("$authDomain/api/v2/users/$id")
        val userConn = userUrl.openConnection() as HttpURLConnection

        userConn.requestMethod = "GET"
        userConn.setRequestProperty("Authorization", "Bearer $token")

        if (userConn.responseCode != 200) {
            throw Exception("Error al obtener usuario ${userConn.responseCode}")
        }

        val userResponse = userConn.inputStream.bufferedReader().use { it.readText() }
        val userJson = JsonParser.parseString(userResponse).asJsonObject

        return when {
            userJson.has("email") -> userJson["email"].asString
            userJson.has("mail") -> userJson["mail"].asString
            else -> "mail_not_found"
        }
    }
}
