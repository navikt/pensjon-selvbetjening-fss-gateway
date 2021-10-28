package no.nav.pensjon.selvbetjening.fssgw.pen

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.micrometer.core.instrument.Metrics
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.oauth2.Oauth2Exception
import org.slf4j.LoggerFactory
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/pen")
class PenController(private val jwsValidator: JwsValidator,
                    private val penConsumer: PenConsumer,
                    private val penConsumerBody: PenConsumerBody) {

    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("springapi/krav")
    fun kravRequest(
            request: HttpServletRequest): ResponseEntity<String> {
        val auth: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        val accessToken: String = auth?.substring("Bearer ".length) ?: ""
        val callId: String? = request.getHeader("Nav-Call-Id")
        val sakstype = request.getParameter("sakstype")

        log.debug("Received request for PEN with correlation ID '$callId'")

        return try {
            val claims = jwsValidator.validate(accessToken)
            val pid: String = getPid(claims)
            val responseBody = penConsumer.callPen("/springapi/krav?sakstype=".plus(sakstype), callId, pid, HttpMethod.GET)
            ResponseEntity(responseBody, jsonContentType, HttpStatus.OK)
        } catch (e: JwtException) {
            unauthorized(e)
        } catch (e: Oauth2Exception) {
            unauthorized(e)
        }
    }

    @GetMapping("springapi/sak/sammendrag")
    fun sammendragRequest(
            request: HttpServletRequest): ResponseEntity<String> {
        val auth: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        val accessToken: String = auth?.substring("Bearer ".length) ?: ""
        val callId: String? = request.getHeader("Nav-Call-Id")

        log.debug("Received request for PEN with correlation ID '$callId'")

        return try {
            val claims = jwsValidator.validate(accessToken)
            val pid: String = getPid(claims)
            val responseBody = penConsumer.callPen("/springapi/sak/sammendrag", callId, pid, HttpMethod.GET)
            ResponseEntity(responseBody, jsonContentType, HttpStatus.OK)
        } catch (e: JwtException) {
            unauthorized(e)
        } catch (e: Oauth2Exception) {
            unauthorized(e)
        }
    }

    @GetMapping("springapi/person/uforehistorikk")
    fun uforehistorikkRequest(
            request: HttpServletRequest): ResponseEntity<String> {
        val auth: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        val accessToken: String = auth?.substring("Bearer ".length) ?: ""
        val callId: String? = request.getHeader("Nav-Call-Id")

        log.debug("Received request for PEN with correlation ID '$callId'")

        return try {
            val claims = jwsValidator.validate(accessToken)
            val pid: String = getPid(claims)
            val responseBody = penConsumer.callPen("/springapi/person/uforehistorikk", callId, pid, HttpMethod.GET)
            ResponseEntity(responseBody, jsonContentType, HttpStatus.OK)
        } catch (e: JwtException) {
            unauthorized(e)
        } catch (e: Oauth2Exception) {
            unauthorized(e)
        }
    }

    @GetMapping("springapi/vedtak/bestemgjeldende")
    fun vedtakGjeldendeRequest(
            request: HttpServletRequest): ResponseEntity<String> {
        val auth: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        val accessToken: String = auth?.substring("Bearer ".length) ?: ""
        val callId: String? = request.getHeader("Nav-Call-Id")
        val fomDato = request.getParameter("fomDato")

        log.debug("Received request for PEN with correlation ID '$callId'")

        return try {
            val claims = jwsValidator.validate(accessToken)
            val pid: String = getPid(claims)
            val responseBody = penConsumer.callPen("/springapi/vedtak/bestemgjeldende", callId, pid, HttpMethod.GET, fomDato)
            ResponseEntity(responseBody, jsonContentType, HttpStatus.OK)
        } catch (e: JwtException) {
            unauthorized(e)
        } catch (e: Oauth2Exception) {
            unauthorized(e)
        }
    }

    @GetMapping("springapi/vedtak")
    fun vedtakRequestSakstype(
            request: HttpServletRequest): ResponseEntity<String> {
        val auth: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        val accessToken: String = auth?.substring("Bearer ".length) ?: ""
        val callId: String? = request.getHeader("Nav-Call-Id")
        val sakstype = request.getParameter("sakstype")
        val alleVedtak = request.getParameter("alleVedtak")
        val kravId = request.getParameter("kravId");

        log.debug("Received request for PEN with correlation ID '$callId'")

        return try {
            val claims = jwsValidator.validate(accessToken)
            val pid: String = getPid(claims)
            val responseBody = penConsumer.callPen("/springapi/vedtak?sakstype=".plus(sakstype)
                .plus("&alleVedtak=$alleVedtak")
                .plus(if(kravId.isNullOrEmpty()) "" else "&kravId=$kravId")
                ,callId, pid, HttpMethod.GET)
            ResponseEntity(responseBody, jsonContentType, HttpStatus.OK)
        } catch (e: JwtException) {
            unauthorized(e)
        } catch (e: Oauth2Exception) {
            unauthorized(e)
        }
    }

    @GetMapping("springapi/ping")
    fun pingRequest(request: HttpServletRequest): ResponseEntity<String> {
        log.debug("Received PEN ping request")

        return try {
            val responseBody = penConsumer.ping("/pen/springapi/ping")
            Metrics.counter("pen_request_counter", "action", "ping", "status", "OK").increment()
            ResponseEntity(responseBody, jsonContentType, HttpStatus.OK)
        } catch (e: PenException) {
            Metrics.counter("pen_request_counter", "action", "ping", "status", "error").increment()
            ResponseEntity("""{"error": "${e.message}"}""", jsonContentType, HttpStatus.BAD_GATEWAY)
        }
    }

    @PostMapping("/api/soknad/alderspensjon/behandle")
    fun forstegangssoknad(
        @RequestBody body: String,
        request: HttpServletRequest): ResponseEntity<String> {
        val auth: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        val accessToken: String = auth?.substring("Bearer ".length) ?: ""
        val callId: String? = request.getHeader("Nav-Call-Id")

        log.debug("Received request for PEN with correlation ID '$callId'")

        return try {
            jwsValidator.validate(accessToken)
            val responseBody = penConsumerBody.callPenClient("/api/soknad/alderspensjon/behandle", body, callId, HttpMethod.POST)
            ResponseEntity(responseBody, jsonContentType, HttpStatus.OK)
        } catch (e: JwtException) {
            unauthorized(e)
        } catch (e: Oauth2Exception) {
            unauthorized(e)
        }
    }

    private fun unauthorized(e: Exception): ResponseEntity<String> {
        log.error("Unauthorized: ${e.message}")
        return ResponseEntity("Unauthorized", HttpStatus.UNAUTHORIZED)
    }

    private fun getPid(claims: Claims) = claims["pid"] as String

    private val jsonContentType: HttpHeaders
        get() {
            val httpHeaders = HttpHeaders()
            httpHeaders.contentType = MediaType.APPLICATION_JSON
            return httpHeaders
        }
}
