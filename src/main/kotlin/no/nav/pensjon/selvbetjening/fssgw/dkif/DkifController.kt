package no.nav.pensjon.selvbetjening.fssgw.dkif

import io.jsonwebtoken.JwtException
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.oauth2.Oauth2Exception
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api")
class DkifController(private val jwsValidator: JwsValidator, private val dkifConsumer: DkifConsumer) {
    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("dkif/api/v1/personer/kontaktinformasjon")
    fun getKontaktinformasjon(
            @RequestParam("inkluderSikkerDigitalPost") inkluderSikkerDigitalPost: Boolean,
            request: HttpServletRequest): ResponseEntity<String> {
        val auth: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        val accessToken: String = auth?.substring("Bearer ".length) ?: ""
        val navCallId: String? = request.getHeader(NAV_CALL_ID)
        val consumerId: String? = request.getHeader(CONSUMER_ID)
        val personIdent: String = request.getHeader(PERSON_IDENTER)
        log.debug("Received request for Dkif with correlation ID '$navCallId")

        try {
            jwsValidator.validate(accessToken)
        } catch (e: JwtException) {
            return unauthorized(e)
        } catch (e: Oauth2Exception) {
            return unauthorized(e)
        }

        val responseBody = dkifConsumer.getKontaktinfo(navCallId, consumerId?: DEFAULT_CONSUMER_ID, inkluderSikkerDigitalPost, personIdent)
        return ResponseEntity(responseBody, jsonContentType, HttpStatus.OK)
    }

    private fun unauthorized(e: Exception): ResponseEntity<String> {
        log.error("Unauthorized: ${e.message}")
        return ResponseEntity("Unauthorized", HttpStatus.UNAUTHORIZED)
    }

    private val jsonContentType: HttpHeaders
        get() {
            val httpHeaders = HttpHeaders()
            httpHeaders.contentType = MediaType.APPLICATION_JSON
            return httpHeaders
        }

    companion object DkifControllerConstants{
        private const val CONSUMER_ID = "Nav-Consumer-Id"
        private const val PERSON_IDENTER = "Nav-Personidenter"
        private const val NAV_CALL_ID = "Nav-Call-Id"
        private const val DEFAULT_CONSUMER_ID = "srvpselv"
    }
}