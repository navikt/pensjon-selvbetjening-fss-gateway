package no.nav.pensjon.selvbetjening.fssgw.esb.unt

import io.jsonwebtoken.JwtException
import io.micrometer.core.instrument.Metrics
import jakarta.security.auth.message.AuthException
import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import no.nav.pensjon.selvbetjening.fssgw.common.XmlEscaper.escapeXml
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.oauth2.Oauth2Exception
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.StringUtils.hasText
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("ws-support")
class UsernameTokenController(
    private val ingressTokenValidator: JwsValidator,
    @Value("\${fg.sts.selfservice.username}") private val serviceUsername: String,
    @Value("\${fg.sts.selfservice.password}") private val servicePassword: String) {

    private val log = KotlinLogging.logger {}

    @GetMapping("unt")
    fun handleGetRequest(request: HttpServletRequest) =
        try {
            checkIngressAuth(request)
            val responseBody = responseBody()
            metric()
            ResponseEntity(responseBody, contentTypeHeaders(), HttpStatus.OK)
        } catch (e: AuthException) {
            unauthorized(e)
        } catch (e: JwtException) {
            unauthorized(e)
        } catch (e: Oauth2Exception) {
            unauthorized(e)
        }

    fun checkIngressAuth(request: HttpServletRequest) {
        val auth = request.getHeader(HttpHeaders.AUTHORIZATION)
        val accessToken = auth?.substring(AUTH_TYPE.length + 1) ?: ""

        if (!hasText(accessToken)) {
            throw JwtException("Missing access token")
        }

        ingressTokenValidator.validate(accessToken)
    }

    private fun responseBody() =
        """<wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" soapenv:mustUnderstand="1">
                <wsse:UsernameToken>
                    <wsse:Username>$serviceUsername</wsse:Username>
                    <wsse:Password Type="$PASSWORD_TYPE">${escapeXml(servicePassword)}</wsse:Password>
                </wsse:UsernameToken>
            </wsse:Security>"""

    private fun unauthorized(e: Exception) = unauthorized(e.message)

    private fun unauthorized(message: String?): ResponseEntity<String> =
        ResponseEntity("Unauthorized", HttpStatus.UNAUTHORIZED)
            .also { log.error { "Unauthorized: $message" } }

    companion object {
        private const val AUTH_TYPE = "Bearer"

        private const val PASSWORD_TYPE =
            "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText"

        private fun contentTypeHeaders() =
            HttpHeaders().apply {
                contentType = MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8)
            }

        private fun metric() =
            Metrics.counter("request_counter", "action", "unt", "status", "OK").increment()
    }
}
