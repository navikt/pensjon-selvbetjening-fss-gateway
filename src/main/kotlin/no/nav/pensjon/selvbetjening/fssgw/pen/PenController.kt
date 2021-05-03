package no.nav.pensjon.selvbetjening.fssgw.pen

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/pen")
class PenController(private val jwsValidator: JwsValidator, private val penConsumer: PenConsumer) {

    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("krav")
    fun kravRequest(
            @RequestBody body: String,
            request: HttpServletRequest): ResponseEntity<String> {
        val auth: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        val accessToken: String = auth?.substring("Bearer ".length) ?: ""
        val callId: String? = request.getHeader("X-Correlation-ID")
        val sakstype = request.getParameter("sakstype")

        log.debug("Received request for PEN with correlation ID '$callId'")

        return try {
            val claims = jwsValidator.validate(accessToken)
            val pid: String = getPid(claims)
            val responseBody = penConsumer.callPen("/krav?sakstype=".plus(sakstype), body, callId, pid)
            ResponseEntity(responseBody, jsonContentType, HttpStatus.OK)
        } catch (e: JwtException) {
            log.error("Unauthorized: ${e.message}")
            ResponseEntity("Unauthorized", HttpStatus.UNAUTHORIZED)
        }
    }

    @GetMapping("krav/{kravId}")
    fun kravRequestId(
            @RequestBody body: String,
            request: HttpServletRequest,
            @PathVariable kravId: Long): ResponseEntity<String> {
        val auth: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        val accessToken: String = auth?.substring("Bearer ".length) ?: ""
        val callId: String? = request.getHeader("X-Correlation-ID")

        log.debug("Received request for PEN with correlation ID '$callId'")

        return try {
            val claims = jwsValidator.validate(accessToken)
            val pid: String = getPid(claims)
            val responseBody = penConsumer.callPen("/krav/".plus(kravId), body, callId, pid)
            ResponseEntity(responseBody, jsonContentType, HttpStatus.OK)
        } catch (e: JwtException) {
            log.error("Unauthorized: ${e.message}")
            ResponseEntity("Unauthorized", HttpStatus.UNAUTHORIZED)
        }
    }

    @GetMapping("sak/sammendrag")
    fun sammendragRequest(
            @RequestBody body: String,
            request: HttpServletRequest): ResponseEntity<String> {
        val auth: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        val accessToken: String = auth?.substring("Bearer ".length) ?: ""
        val callId: String? = request.getHeader("X-Correlation-ID")

        log.debug("Received request for PEN with correlation ID '$callId'")

        return try {
            val claims = jwsValidator.validate(accessToken)
            val pid: String = getPid(claims)
            val responseBody = penConsumer.callPen("/sak/sammendrag", body, callId, pid)
            ResponseEntity(responseBody, jsonContentType, HttpStatus.OK)
        } catch (e: JwtException) {
            log.error("Unauthorized: ${e.message}")
            ResponseEntity("Unauthorized", HttpStatus.UNAUTHORIZED)
        }
    }

    @GetMapping("person/uforehistorikk")
    fun uforehistorikkRequest(
            @RequestBody body: String,
            request: HttpServletRequest): ResponseEntity<String> {
        val auth: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        val accessToken: String = auth?.substring("Bearer ".length) ?: ""
        val callId: String? = request.getHeader("X-Correlation-ID")

        log.debug("Received request for PEN with correlation ID '$callId'")

        return try {
            val claims = jwsValidator.validate(accessToken)
            val pid: String = getPid(claims)
            val responseBody = penConsumer.callPen("/person/uforehistorikk", body, callId, pid)
            ResponseEntity(responseBody, jsonContentType, HttpStatus.OK)
        } catch (e: JwtException) {
            log.error("Unauthorized: ${e.message}")
            ResponseEntity("Unauthorized", HttpStatus.UNAUTHORIZED)
        }
    }

    @GetMapping("vedtak/bestemgjeldende")
    fun vedtakGjeldendeRequest(
            @RequestBody body: String,
            request: HttpServletRequest): ResponseEntity<String> {
        val auth: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        val accessToken: String = auth?.substring("Bearer ".length) ?: ""
        val callId: String? = request.getHeader("X-Correlation-ID")
        val fomDato = request.getParameter("fomDato")

        log.debug("Received request for PEN with correlation ID '$callId'")

        return try {
            val claims = jwsValidator.validate(accessToken)
            val pid: String = getPid(claims)
            val responseBody = penConsumer.callPen("/vedtak/bestemgjeldende", body, callId, pid, fomDato)
            ResponseEntity(responseBody, jsonContentType, HttpStatus.OK)
        } catch (e: JwtException) {
            log.error("Unauthorized: ${e.message}")
            ResponseEntity("Unauthorized", HttpStatus.UNAUTHORIZED)
        }
    }

    @GetMapping("vedtak")
    fun vedtakRequestSakstype(
            @RequestBody body: String,
            request: HttpServletRequest): ResponseEntity<String> {
        val auth: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        val accessToken: String = auth?.substring("Bearer ".length) ?: ""
        val callId: String? = request.getHeader("X-Correlation-ID")
        val sakstype = request.getParameter("sakstype")

        log.debug("Received request for PEN with correlation ID '$callId'")

        return try {
            val claims = jwsValidator.validate(accessToken)
            val pid: String = getPid(claims)
            val responseBody = penConsumer.callPen("/vedtak?=".plus(sakstype), body, callId, pid)
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
