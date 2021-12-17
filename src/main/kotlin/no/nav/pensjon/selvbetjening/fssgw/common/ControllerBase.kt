package no.nav.pensjon.selvbetjening.fssgw.common

import io.jsonwebtoken.JwtException
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.oauth2.Oauth2Exception
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.StringUtils.hasText
import java.util.*
import javax.servlet.http.HttpServletRequest

abstract class ControllerBase(
    private val jwsValidator: JwsValidator,
    private val consumer: ServiceClient,
    private val egressTokenGetter: ServiceTokenGetter,
    private val egressEndpoint: String) {

    private val authType = "Bearer"
    private val callIdHeaderName = "Nav-Call-Id"
    private val log = LoggerFactory.getLogger(javaClass)

    private val notRelayedHeaders = listOf(
        HttpHeaders.ACCEPT.toLowerCase(),
        HttpHeaders.AUTHORIZATION.toLowerCase(),
        HttpHeaders.HOST.toLowerCase(),
        HttpHeaders.USER_AGENT.toLowerCase()
    )

    fun handle(request: HttpServletRequest): ResponseEntity<String> {
        return try {
            checkIngressAuth(request)
            val headersToRelay = getEgressHeaders(request)
            val queryPart = if (hasText(request.queryString)) "?${request.queryString}" else ""
            val urlSuffix = "$egressEndpoint${request.requestURI}$queryPart"
            val responseBody = consumer.callService(urlSuffix, headersToRelay)
            ResponseEntity(responseBody, jsonContentType, HttpStatus.OK)
        } catch (e: JwtException) {
            unauthorized(e)
        } catch (e: Oauth2Exception) {
            unauthorized(e)
        }
    }

    protected abstract fun egressAuthWaived(): Boolean

    private fun getEgressHeaders(request: HttpServletRequest): HashMap<String, String> {
        val egressHeaders = HashMap<String, String>()
        request.headerNames.toList().forEach { copyHeader(request, it, egressHeaders) }
        addAuthHeaderIfNeeded(egressHeaders)
        addCallIdHeaderIfNeeded(egressHeaders)
        return egressHeaders
    }

    private fun addAuthHeaderIfNeeded(headers: HashMap<String, String>) {
        if (egressAuthWaived()) {
            return
        }

        val token = egressTokenGetter.getServiceUserToken().accessToken
        headers[HttpHeaders.AUTHORIZATION] = "$authType $token"
    }

    private fun addCallIdHeaderIfNeeded(headers: HashMap<String, String>) {
        if (headers.containsKey(callIdHeaderName)) {
            return
        }

        headers[callIdHeaderName] = UUID.randomUUID().toString()
    }

    private fun copyHeader(request: HttpServletRequest, headerName: String, headers: HashMap<String, String>) {
        if (notRelayedHeaders.contains(headerName.toLowerCase())) {
            return
        }

        headers[headerName] = request.getHeader(headerName)
    }

    private fun checkIngressAuth(request: HttpServletRequest) {
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
