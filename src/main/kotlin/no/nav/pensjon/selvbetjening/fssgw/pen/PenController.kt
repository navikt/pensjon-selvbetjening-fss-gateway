package no.nav.pensjon.selvbetjening.fssgw.pen

import io.jsonwebtoken.Claims
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
class PenController(private val jwsValidator: JwsValidator, private val penConsumer: PenConsumer) {

    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping("pen")
    fun penRequest(
            @RequestBody body: String,
            request: HttpServletRequest): ResponseEntity<String> {
        val auth: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        val accessToken: String = auth?.substring("Bearer ".length) ?: ""
        val callId: String? = request.getHeader("X-Correlation-ID")
        log.debug("Received request for PEN with correlation ID '$callId'")

        return try {
            val claims = jwsValidator.validate(accessToken)
            val pid: String = getPid(claims)
            val responseBody = penConsumer.callPen(body, callId, pid)
            ResponseEntity(responseBody, jsonContentType, HttpStatus.OK)
        } catch (e: JwtException) {
            log.error("Unauthorized: ${e.message}")
            ResponseEntity("Unauthorized", HttpStatus.UNAUTHORIZED)
        }
    }

    private fun getPid(claims: Claims) = claims["pid"] as String

    private val jsonContentType: HttpHeaders
        get() {
            val httpHeaders = HttpHeaders()
            httpHeaders.contentType = MediaType.APPLICATION_JSON
            return httpHeaders
        }
}
