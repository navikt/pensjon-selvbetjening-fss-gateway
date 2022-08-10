package no.nav.pensjon.selvbetjening.fssgw.common

import io.jsonwebtoken.JwtException
import io.micrometer.core.instrument.Metrics
import no.nav.pensjon.selvbetjening.fssgw.tech.oauth2.Oauth2Exception
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.StringUtils.hasText
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.UUID.randomUUID
import javax.security.auth.message.AuthException
import javax.servlet.http.HttpServletRequest

abstract class ControllerBase(
    private val serviceClient: ServiceClient,
    private val egressEndpoint: String) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val locale = Locale.getDefault()

    private val notRelayedHeaders = listOf(
        HttpHeaders.ACCEPT.lowercase(locale),
        HttpHeaders.AUTHORIZATION.lowercase(locale),
        HttpHeaders.HOST.lowercase(locale),
        HttpHeaders.USER_AGENT.lowercase(locale),
        HttpHeaders.CONTENT_LENGTH.lowercase(locale),
        CONSUMER_TOKEN_HEADER_NAME.lowercase(locale)
    )

    fun doGet(request: HttpServletRequest): ResponseEntity<String> {
        return try {
            checkIngressAuth(request)
            val headersToRelay = getEgressHeaders(request)
            val queryPart = if (hasText(request.queryString)) "?${request.queryString}" else ""
            val url = "$egressEndpoint${request.requestURI}$queryPart"
            val responseBody = serviceClient.doGet(url, headersToRelay)
            val responseContentType = getResponseContentType(request)
            metric("get", "OK")
            ResponseEntity(responseBody, responseContentType, HttpStatus.OK)
        } catch (e: AuthException) {
            unauthorized(e)
        } catch (e: JwtException) {
            unauthorized(e)
        } catch (e: Oauth2Exception) {
            unauthorized(e)
        } catch (e: ConsumerException) {
            metric("get", "error")
            ResponseEntity("""{"error": "${e.message}"}""", jsonContentType, HttpStatus.BAD_GATEWAY)
        } finally {
            MDC.clear()
        }
    }

    fun doOptions(request: HttpServletRequest): ResponseEntity<String> {
        return try {
            checkIngressAuth(request)
            val headersToRelay = getEgressHeaders(request)
            val queryPart = if (hasText(request.queryString)) "?${request.queryString}" else ""
            val url = "$egressEndpoint${request.requestURI}$queryPart"
            val responseBody = serviceClient.doOptions(url, headersToRelay)
            val responseContentType = getResponseContentType(request)
            metric("options", "OK")
            ResponseEntity(responseBody, responseContentType, HttpStatus.OK)
        } catch (e: AuthException) {
            unauthorized(e)
        } catch (e: JwtException) {
            unauthorized(e)
        } catch (e: Oauth2Exception) {
            unauthorized(e)
        } catch (e: ConsumerException) {
            metric("get", "error")
            ResponseEntity("""{"error": "${e.message}"}""", jsonContentType, HttpStatus.BAD_GATEWAY)
        } finally {
            MDC.clear()
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
            metric("post", "OK")
            ResponseEntity(responseBody, responseContentType, HttpStatus.OK)
        } catch (e: AuthException) {
            unauthorized(e)
        } catch (e: JwtException) {
            unauthorized(e)
        } catch (e: Oauth2Exception) {
            unauthorized(e)
        } catch (e: ConsumerException) {
            metric("post", "error")
            ResponseEntity("""{"error": "${e.message}"}""", jsonContentType, HttpStatus.BAD_GATEWAY)
        } finally {
            MDC.clear()
        }
    }

    protected abstract fun checkIngressAuth(request: HttpServletRequest)

    protected abstract fun addAuthHeaderIfNeeded(request: HttpServletRequest, headers: TreeMap<String, String>)

    private fun getEgressHeaders(request: HttpServletRequest): TreeMap<String, String> {
        val egressHeaders = TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER)
        request.headerNames.toList().forEach { copyHeader(request, it, egressHeaders) }
        addAuthHeaderIfNeeded(request, egressHeaders)
        addCallIdHeader(egressHeaders)
        return egressHeaders
    }

    private fun getResponseContentType(request: HttpServletRequest): HttpHeaders {
        val requestContentType: String? = request.getHeader(HttpHeaders.CONTENT_TYPE)

        return if (requestContentType != null && requestContentType.startsWith(MediaType.TEXT_XML_VALUE))
            xmlContentType else jsonContentType
    }

    private fun addCallIdHeader(headers: TreeMap<String, String>) {
        val callId = resolveCallId(headers)
        MDC.put(CALL_ID_HEADER_NAME_1, callId)
        headers[CALL_ID_HEADER_NAME_1] = callId
    }

    private fun copyHeader(request: HttpServletRequest, headerName: String, headers: TreeMap<String, String>) {
        if (notRelayedHeaders.contains(headerName.lowercase(locale))) {
            return
        }

        headers[headerName] = request.getHeader(headerName)
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

    private fun metric(action: String, status: String) {
        Metrics.counter("request_counter", "action", action, "status", status).increment()
    }

    private val jsonContentType: HttpHeaders
        get() = contentTypeHeaders(MediaType.APPLICATION_JSON)

    private val xmlContentType: HttpHeaders
        get() = contentTypeHeaders(MediaType(MediaType.TEXT_XML, StandardCharsets.UTF_8))

    private fun resolveCallId(headers: TreeMap<String, String>): String {
        return NAV_CALL_ID_HEADER_NAMES
            .asSequence()
            .mapNotNull { headers[it] }
            .firstOrNull { it.isNotEmpty() }
            ?: randomUUID().toString()
    }

    companion object {
        const val CALL_ID_HEADER_NAME_1 = "Nav-Call-Id"
        const val CONSUMER_TOKEN_HEADER_NAME = "Nav-Consumer-Token"

        // NB: No consensus in NAV regarding call ID header name,
        // ref. https://github.com/navikt/k9-formidling/blob/master/app/src/main/kotlin/no/nav/k9/formidling/app/logging/LoggingHjelper.kt

        val NAV_CALL_ID_HEADER_NAMES =
            setOf(
                CALL_ID_HEADER_NAME_1,
                "Nav-CallId",
                "Nav-Callid",
                "X-Correlation-Id")
    }
}
