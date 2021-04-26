package no.nav.pensjon.selvbetjening.fssgw.pen

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

        try {
            jwsValidator.validate(accessToken)
        } catch (e: JwtException) {
            log.error("Unauthorized: ${e.message}")
            return ResponseEntity("Unauthorized", HttpStatus.UNAUTHORIZED)
        }

        val pid: String = getPid(accessToken)
        val responseBody = penConsumer.callPen(body, callId, pid)
        return ResponseEntity(responseBody, jsonContentType, HttpStatus.OK)
    }

    private fun getPid(accessToken: String): String {
        TODO("Not yet implemented")
    }

    private val jsonContentType: HttpHeaders
        get() {
            val httpHeaders = HttpHeaders()
            httpHeaders.contentType = MediaType.APPLICATION_JSON
            return httpHeaders
        }
}
