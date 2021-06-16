package no.nav.pensjon.selvbetjening.fssgw.tps

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
@RequestMapping("/api/tps")
class TpsController(private val jwsValidator: JwsValidator, private val tpsConsumer: TpsConsumer) {

    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping("person")
    fun personRequest(
            @RequestBody body: String,
            request: HttpServletRequest): ResponseEntity<String> {
        val auth: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        val accessToken: String = auth?.substring("Bearer ".length) ?: ""
        log.debug("Received TPS person request")

        try {
            jwsValidator.validate(accessToken)
        } catch (e: JwtException) {
            return unauthorized(e)
        } catch (e: Oauth2Exception) {
            return unauthorized(e)
        }

        val responseBody = tpsConsumer.getPerson(body)
        Metrics.counter("tps_request_counter", "action", "person", "status", "OK").increment()
        return ResponseEntity(responseBody, jsonContentType, HttpStatus.OK)
    }

    @PostMapping("ping")
    fun pingRequest(
            @RequestBody body: String,
            request: HttpServletRequest): ResponseEntity<String> {
        log.debug("Received TPS ping request")
        val responseBody = tpsConsumer.ping(body)
        Metrics.counter("tps_request_counter", "action", "ping", "status", "OK").increment()
        return ResponseEntity(responseBody, jsonContentType, HttpStatus.OK)
    }

    private fun unauthorized(e: Exception): ResponseEntity<String> {
        log.error("Unauthorized: ${e.message}")
        Metrics.counter("tps_request_counter", "action", "person", "status", "Unauthorized").increment()
        return ResponseEntity("Unauthorized", HttpStatus.UNAUTHORIZED)
    }

    private val jsonContentType: HttpHeaders
        get() {
            val httpHeaders = HttpHeaders()
            httpHeaders.contentType = MediaType.APPLICATION_JSON
            return httpHeaders
        }
}
