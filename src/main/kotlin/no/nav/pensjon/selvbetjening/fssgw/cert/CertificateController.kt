package no.nav.pensjon.selvbetjening.fssgw.cert

import io.jsonwebtoken.JwtException
import jakarta.security.auth.message.AuthException
import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.oauth2.Oauth2Exception
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.StringUtils.hasText
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("cert-support")
class CertificateController(private val ingressTokenValidator: JwsValidator) {

    private val log = KotlinLogging.logger {}

    @GetMapping("credentials")
    fun handleCredentialsRequest(request: HttpServletRequest) =
        try {
            checkIngressAuth(request)
            val responseBody = credentialsResponseBody()
            ResponseEntity(responseBody, contentTypeHeaders(), HttpStatus.OK)
        } catch (e: AuthException) {
            unauthorized(e)
        } catch (e: JwtException) {
            unauthorized(e)
        } catch (e: Oauth2Exception) {
            unauthorized(e)
        }

    @GetMapping("p12")
    fun handleP12Request(request: HttpServletRequest) =
        try {
            checkIngressAuth(request)
            val responseBody = p12ResponseBody()
            ResponseEntity(responseBody, contentTypeHeaders(), HttpStatus.OK)
        } catch (e: AuthException) {
            unauthorized(e)
        } catch (e: JwtException) {
            unauthorized(e)
        } catch (e: Oauth2Exception) {
            unauthorized(e)
        }

    private fun checkIngressAuth(request: HttpServletRequest) {
        val auth = request.getHeader(HttpHeaders.AUTHORIZATION)
        val accessToken = auth?.substring(AUTH_TYPE.length + 1) ?: ""

        if (hasText(accessToken).not()) {
            throw JwtException("Missing access token")
        }

        ingressTokenValidator.validate(accessToken)
    }

    private fun credentialsResponseBody() =
        File("$PATH/credentials_2024-03.json").readText()

    private fun p12ResponseBody() =
        File("$PATH/1956916119612047183131283-2024-03-19.p12.b64").readText()

    private fun unauthorized(e: Exception) =
        unauthorized(e.message)

    private fun unauthorized(message: String?): ResponseEntity<String> =
        ResponseEntity("Unauthorized", HttpStatus.UNAUTHORIZED)
            .also { log.error { "Unauthorized: $message" } }

    private companion object {
        private const val PATH = "/secrets/virksomhetssertifikat"
        private const val AUTH_TYPE = "Bearer"

        private fun contentTypeHeaders() =
            HttpHeaders().apply {
                contentType = MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8)
            }
    }
}
