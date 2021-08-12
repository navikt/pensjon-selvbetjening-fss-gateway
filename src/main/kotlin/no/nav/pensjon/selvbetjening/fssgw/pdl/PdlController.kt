package no.nav.pensjon.selvbetjening.fssgw.pdl

import io.jsonwebtoken.JwtException
import io.micrometer.core.instrument.Metrics
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.oauth2.Oauth2Exception
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api")
class PdlController(private val jwsValidator: JwsValidator, private val pdlConsumer: PdlConsumer) {

    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping("pdl")
    fun pdlRequest(
            @RequestBody body: String,
            request: HttpServletRequest): ResponseEntity<String> {
        val auth: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        val accessToken: String = auth?.substring("Bearer ".length) ?: ""
        val callId: String? = request.getHeader("Nav-Call-Id")
        log.debug("Received request for PDL with correlation ID '$callId'")

        try {
            jwsValidator.validate(accessToken)
        } catch (e: JwtException) {
            return unauthorized(e)
        } catch (e: Oauth2Exception) {
            return unauthorized(e)
        }

        val responseBody = pdlConsumer.callPdl(body, callId)
        Metrics.counter("pdl_request_counter", "status", "OK").increment()
        return ResponseEntity(responseBody, jsonContentType, HttpStatus.OK)
    }

    private fun unauthorized(e: Exception): ResponseEntity<String> {
        log.error("Unauthorized: ${e.message}")
        Metrics.counter("pdl_request_counter", "status", "Unauthorized").increment()
        return ResponseEntity("Unauthorized", HttpStatus.UNAUTHORIZED)
    }

    private val jsonContentType: HttpHeaders
        get() {
            val httpHeaders = HttpHeaders()
            httpHeaders.contentType = MediaType.APPLICATION_JSON
            return httpHeaders
        }
}
