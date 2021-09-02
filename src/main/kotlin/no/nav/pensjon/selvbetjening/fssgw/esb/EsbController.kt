package no.nav.pensjon.selvbetjening.fssgw.esb

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
@RequestMapping("/api/esb")
class EsbController(private val jwsValidator: JwsValidator, private val consumer: EsbConsumer) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val fullmaktPath = "/nav-cons-pen-pselv-fullmaktWeb/sca/PSELVFullmaktWSEXP"
    private val personPath = "/nav-cons-pen-pselv-personWeb/sca/PSELVPersonWSEXP"
    private val pingPath = "/nav-cons-test-getapplicationversionWeb/sca/TESTGetApplicationVersionWSEXP"

    @PostMapping("fullmakt")
    fun fullmaktRequest(
        @RequestBody body: String,
        request: HttpServletRequest
    ): ResponseEntity<String> {
        log.debug("Received ESB fullmakt request")
        Metrics.counter("esb_request_counter", "action", "fullmakt", "status", "OK").increment()
        return callEsb(fullmaktPath, body, request)
    }

    @PostMapping("person")
    fun personRequest(
        @RequestBody body: String,
        request: HttpServletRequest
    ): ResponseEntity<String> {
        log.debug("Received ESB person request")
        Metrics.counter("esb_request_counter", "action", "person", "status", "OK").increment()
        return callEsb(personPath, body, request)
    }

    @PostMapping("ping")
    fun pingRequest(
        @RequestBody body: String,
        request: HttpServletRequest
    ): ResponseEntity<String> {
        log.debug("Received ESB ping request")
        val responseBody = consumer.callEsb(pingPath, body)
        Metrics.counter("esb_request_counter", "action", "ping", "status", "OK").increment()
        return ResponseEntity(responseBody, jsonContentType, HttpStatus.OK)
    }

    private fun callEsb(
        path: String,
        body: String,
        request: HttpServletRequest
    ): ResponseEntity<String> {
        val auth: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        val accessToken: String = auth?.substring("Bearer ".length) ?: ""

        try {
            jwsValidator.validate(accessToken)
        } catch (e: JwtException) {
            return unauthorized(e)
        } catch (e: Oauth2Exception) {
            return unauthorized(e)
        }

        val responseBody = consumer.callEsb(path, body)
        return ResponseEntity(responseBody, jsonContentType, HttpStatus.OK)
    }

    private fun unauthorized(e: Exception): ResponseEntity<String> {
        log.error("Unauthorized: ${e.message}")
        Metrics.counter("esb_request_counter", "action", "person", "status", "Unauthorized").increment()
        return ResponseEntity("Unauthorized", HttpStatus.UNAUTHORIZED)
    }

    private val jsonContentType: HttpHeaders
        get() {
            val httpHeaders = HttpHeaders()
            httpHeaders.contentType = MediaType.APPLICATION_JSON
            return httpHeaders
        }
}
