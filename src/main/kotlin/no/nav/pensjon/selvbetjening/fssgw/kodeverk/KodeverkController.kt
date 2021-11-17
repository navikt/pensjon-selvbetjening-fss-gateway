package no.nav.pensjon.selvbetjening.fssgw.kodeverk

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
class KodeverkController(private val jwsValidator: JwsValidator, private val kodeverkConsumer: KodeverkConsumer) {
    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/kodeverk/Postnummer/koder/betydninger")
    fun getBetydningerForPostnummer(
            @RequestParam("spraak") sprak : String,
            request: HttpServletRequest): ResponseEntity<String> {
        val auth: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        val accessToken: String = auth?.substring("Bearer ".length) ?: ""
        val navCallId: String? = request.getHeader("Nav-Call-Id")
        val navConsumerId: String? = request.getHeader("Nav-Consumer-Id")
        log.debug("Received request for Kodeverk with correlation ID '$navCallId")

        try {
            jwsValidator.validate(accessToken)
        } catch (e: JwtException) {
            return unauthorized(e)
        } catch (e: Oauth2Exception) {
            return unauthorized(e)
        }

        val response = kodeverkConsumer.getBetydningerForPostnummer(navCallId, navConsumerId, sprak)
        return ResponseEntity(response, jsonContentType, HttpStatus.OK)
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
}
