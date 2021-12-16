package no.nav.pensjon.selvbetjening.fssgw.skjerm

import io.jsonwebtoken.JwtException
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.oauth2.Oauth2Exception
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/skjermet")
class SkjermingController(
    private val jwsValidator: JwsValidator,
    private val consumer: SkjermingConsumer
) {
    private val authType = "Bearer"
    private val callIdHeaderName = "Nav-Call-Id"
    private val log = LoggerFactory.getLogger(javaClass)

    private val ignoredHeaders = listOf(
        HttpHeaders.ACCEPT.toLowerCase(),
        HttpHeaders.HOST.toLowerCase(),
        HttpHeaders.USER_AGENT.toLowerCase()
    )

    @GetMapping
    fun isSkjermet(request: HttpServletRequest): ResponseEntity<String> {
        return try {
            checkAuth(request)
            val headersToRelay = getEgressHeaders(request)
            val urlSuffix = "${request.requestURI}?${request.queryString}"
            val responseBody = consumer.isSkjermet(urlSuffix, headersToRelay)
            ResponseEntity(responseBody, jsonContentType, HttpStatus.OK)
        } catch (e: JwtException) {
            unauthorized(e)
        } catch (e: Oauth2Exception) {
            unauthorized(e)
        }
    }

    private fun getEgressHeaders(request: HttpServletRequest): HashMap<String, String> {
        val egressHeaders = HashMap<String, String>()
        request.headerNames.toList().forEach { copyHeader(request, it, egressHeaders) }
        addCallIdHeaderIfNeeded(egressHeaders)
        return egressHeaders
    }

    private fun addCallIdHeaderIfNeeded(headers: HashMap<String, String>) {
        if (headers.containsKey(callIdHeaderName)) {
            return
        }

        headers.put(callIdHeaderName, UUID.randomUUID().toString())
    }

    private fun copyHeader(request: HttpServletRequest, headerName: String, headers: HashMap<String, String>) {
        if (ignoredHeaders.contains(headerName.toLowerCase())) {
            return
        }

        headers[headerName] = request.getHeader(headerName)
    }

    private fun checkAuth(request: HttpServletRequest) {
        val auth: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        val accessToken: String = auth?.substring(authType.length + 1) ?: ""
        jwsValidator.validate(accessToken)
    }

    private fun unauthorized(e: Exception): ResponseEntity<String> {
        return unauthorized(e.message)
    }

    private fun unauthorized(message: String?): ResponseEntity<String> {
        log.error("Unauthorized: $message")
        return ResponseEntity("Unauthorized", HttpStatus.UNAUTHORIZED)
    }

    private val jsonContentType: HttpHeaders
        get() {
            val httpHeaders = HttpHeaders()
            httpHeaders.contentType = MediaType.APPLICATION_JSON
            return httpHeaders
        }
}
