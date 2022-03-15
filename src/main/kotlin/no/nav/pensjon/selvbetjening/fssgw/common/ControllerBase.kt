package no.nav.pensjon.selvbetjening.fssgw.common

import io.jsonwebtoken.JwtException
import io.micrometer.core.instrument.Metrics
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.oauth2.Oauth2Exception
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.StringUtils.hasText
import java.nio.charset.StandardCharsets
import java.util.*
import javax.servlet.http.HttpServletRequest

abstract class ControllerBase(
    private val jwsValidator: JwsValidator,
    private val serviceClient: ServiceClient,
    private val egressTokenGetter: ServiceTokenGetter,
    private val egressEndpoint: String) {

    private val authType = "Bearer"
    private val callIdHeaderName = "Nav-Call-Id"
    private val consumerTokenHeaderName = "Nav-Consumer-Token"
    private val log = LoggerFactory.getLogger(javaClass)
    private val locale = Locale.getDefault()

    private val notRelayedHeaders = listOf(
        HttpHeaders.ACCEPT.lowercase(locale),
        HttpHeaders.AUTHORIZATION.lowercase(locale),
        HttpHeaders.HOST.lowercase(locale),
        HttpHeaders.USER_AGENT.lowercase(locale),
        HttpHeaders.CONTENT_LENGTH.lowercase(locale),
        consumerTokenHeaderName.lowercase(locale)
    )

    fun doGet(request: HttpServletRequest): ResponseEntity<String> {
        return try {
            checkIngressAuth(request)
            val headersToRelay = getEgressHeaders(request)
            val queryPart = if (hasText(request.queryString)) "?${request.queryString}" else ""
            val url = "$egressEndpoint${request.requestURI}$queryPart"
            val responseBody = serviceClient.doGet(url, headersToRelay)
            val responseContentType = getResponseContentType(request)
            Metrics.counter("request_counter", "action", "get", "status", "OK").increment()
            ResponseEntity(responseBody, responseContentType, HttpStatus.OK)
        } catch (e: JwtException) {
            unauthorized(e)
        } catch (e: Oauth2Exception) {
            unauthorized(e)
        } catch (e: ConsumerException) {
            Metrics.counter("request_counter", "action", "get", "status", "error").increment()
            ResponseEntity("""{"error": "${e.message}"}""", jsonContentType, HttpStatus.BAD_GATEWAY)
        }
    }

    fun doPost(request: HttpServletRequest, body: String): ResponseEntity<String> {
        return try {
            checkIngressAuth(request)
            val headersToRelay = getEgressHeaders(request)
            val queryPart = if (hasText(request.queryString)) "?${request.queryString}" else ""
            val url = "$egressEndpoint${request.requestURI}$queryPart"
            val responseBody = serviceClient.doPost(url, headersToRelay, body)
            val responseContentType = getResponseContentType(request)
            Metrics.counter("request_counter", "action", "post", "status", "OK").increment()
            ResponseEntity(responseBody, responseContentType, HttpStatus.OK)
        } catch (e: JwtException) {
            unauthorized(e)
        } catch (e: Oauth2Exception) {
            unauthorized(e)
        } catch (e: ConsumerException) {
            Metrics.counter("request_counter", "action", "post", "status", "error").increment()
            ResponseEntity("""{"error": "${e.message}"}""", jsonContentType, HttpStatus.BAD_GATEWAY)
        }
    }

    protected abstract fun egressAuthWaived(): Boolean

    protected abstract fun consumerTokenRequired(): Boolean

    private fun getEgressHeaders(request: HttpServletRequest): TreeMap<String, String> {
        val egressHeaders = TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER)
        request.headerNames.toList().forEach { copyHeader(request, it, egressHeaders) }
        addAuthHeaderIfNeeded(egressHeaders)
        addCallIdHeaderIfNeeded(egressHeaders)
        return egressHeaders
    }

    private fun getResponseContentType(request: HttpServletRequest): HttpHeaders {
        val requestContentType: String? = request.getHeader(HttpHeaders.CONTENT_TYPE)

        return if (requestContentType != null && requestContentType.startsWith(MediaType.TEXT_XML_VALUE))
            xmlContentType else jsonContentType
    }

    private fun addAuthHeaderIfNeeded(headers: TreeMap<String, String>) {
        if (egressAuthWaived()) {
            return
        }

        val token = egressTokenGetter.getServiceUserToken().accessToken
        val auth = "$authType $token"
        headers[HttpHeaders.AUTHORIZATION] = auth

        if (consumerTokenRequired()) {
            headers[consumerTokenHeaderName] = auth
        }
    }

    private fun addCallIdHeaderIfNeeded(headers: TreeMap<String, String>) {
        if (headers.containsKey(callIdHeaderName)) {
            return
        }

        headers[callIdHeaderName] = UUID.randomUUID().toString()
    }

    private fun copyHeader(request: HttpServletRequest, headerName: String, headers: TreeMap<String, String>) {
        if (notRelayedHeaders.contains(headerName.lowercase(locale))) {
            return
        }

        headers[headerName] = request.getHeader(headerName)
    }

    private fun checkIngressAuth(request: HttpServletRequest) {
        val auth: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        val accessToken: String = auth?.substring(authType.length + 1) ?: ""

        if (!hasText(accessToken)) {
            throw JwtException("Missing access token")
        }

        jwsValidator.validate(accessToken)
    }

    private fun unauthorized(e: Exception): ResponseEntity<String> {
        return unauthorized(e.message)
    }

    private fun unauthorized(message: String?): ResponseEntity<String> {
        log.error("Unauthorized: $message")
        return ResponseEntity("Unauthorized", HttpStatus.UNAUTHORIZED)
    }

    private fun contentTypeHeaders(mediaType: MediaType): HttpHeaders {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = mediaType
        return httpHeaders
    }

    private val jsonContentType: HttpHeaders
        get() = contentTypeHeaders(MediaType.APPLICATION_JSON)

    private val xmlContentType: HttpHeaders
        get() = contentTypeHeaders(MediaType(MediaType.TEXT_XML, StandardCharsets.UTF_8))
}
