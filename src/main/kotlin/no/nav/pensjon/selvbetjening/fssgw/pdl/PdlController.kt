package no.nav.pensjon.selvbetjening.fssgw.pdl

import io.jsonwebtoken.JwtException
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
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
        val callId: String? = request.getHeader("X-Correlation-ID")
        log.debug("Received request for PDL with correlation ID '$callId'")

        try {
            jwsValidator.validate(accessToken)
        } catch (e: JwtException) {
            log.error("Unauthorized: ${e.message}")
            return ResponseEntity("Unauthorized", HttpStatus.UNAUTHORIZED)
        }

        val responseBody = pdlConsumer.callPdl(body, callId)
        return ResponseEntity(responseBody, jsonContentType, HttpStatus.OK)
    }

    private val jsonContentType: HttpHeaders
        get() {
            val httpHeaders = HttpHeaders()
            httpHeaders.contentType = MediaType.APPLICATION_JSON
            return httpHeaders
        }
}
