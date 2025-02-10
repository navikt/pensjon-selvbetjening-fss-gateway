package no.nav.pensjon.selvbetjening.fssgw.common

import io.jsonwebtoken.JwtException
import io.micrometer.core.instrument.Metrics
import jakarta.security.auth.message.AuthException
import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import no.nav.pensjon.selvbetjening.fssgw.tech.oauth2.Oauth2Exception
import org.slf4j.MDC
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.StringUtils.hasText
import java.nio.charset.StandardCharsets
import java.util.*

abstract class ControllerBase(
    private val serviceClient: ServiceClient,
    private val callIdGenerator: CallIdGenerator,
    private val egressEndpoint: String
) {
    private val log = KotlinLogging.logger {}

    fun doGet(request: HttpServletRequest): ResponseEntity<String> {
        val responseContentType: HttpHeaders = getResponseContentType(request)

        return try {
            val authorizedParty = checkIngressAuth(request)
            val headersToRelay = getEgressHeaders(request, DEFAULT_SERVICE_USER_ID)
            val queryPart = if (hasText(request.queryString)) "?${request.queryString}" else ""
            val url = "$egressEndpoint${request.requestURI}$queryPart"
            val responseBody = serviceClient.doGet(url, headersToRelay)
            metric("GET ${metricDetail(request)}", authorizedParty)
            ResponseEntity(responseBody, responseContentType, HttpStatus.OK)
        } catch (e: AuthException) {
            unauthorized(e)
        } catch (e: JwtException) {
            unauthorized(e)
        } catch (e: Oauth2Exception) {
            unauthorized(e)
        } catch (e: EgressException) {
            handleError(request, "GET", e, responseContentType)
        } finally {
            MDC.clear()
        }
    }

    fun doOptions(request: HttpServletRequest): ResponseEntity<String> {
        val responseContentType: HttpHeaders = getResponseContentType(request)

        return try {
            val authorizedParty = checkIngressAuth(request)
            val headersToRelay = getEgressHeaders(request, DEFAULT_SERVICE_USER_ID)
            val queryPart = if (hasText(request.queryString)) "?${request.queryString}" else ""
            val url = "$egressEndpoint${request.requestURI}$queryPart"
            val responseBody = serviceClient.doOptions(url, headersToRelay)
            metric("OPTIONS ${metricDetail(request)}", authorizedParty)
            ResponseEntity(responseBody, responseContentType, HttpStatus.OK)
        } catch (e: AuthException) {
            unauthorized(e)
        } catch (e: JwtException) {
            unauthorized(e)
        } catch (e: Oauth2Exception) {
            unauthorized(e)
        } catch (e: EgressException) {
            handleError(request, "OPTIONS", e, responseContentType)
        } finally {
            MDC.clear()
        }
    }

    fun doPost(request: HttpServletRequest, body: String, serviceUserId: Int = 1, externalCall: Boolean = false): ResponseEntity<String> {
        val responseContentType: HttpHeaders = getResponseContentType(request)

        return try {
            val authorizedParty = checkIngressAuth(request)
            val headersToRelay = getEgressHeaders(request, serviceUserId)
            val queryPart = if (hasText(request.queryString)) "?${request.queryString}" else ""
            val url = "$egressEndpoint${request.requestURI}$queryPart"
            val responseBody = serviceClient.doPost(url, headersToRelay, provideBodyAuth(body), externalCall)
            metric("POST ${metricDetail(request)}", authorizedParty)
            if (request.requestURI.contains("TrekkWSEXP")) {
                log.info("Response: $responseBody")
            }

            ResponseEntity(responseBody, responseContentType, HttpStatus.OK)
        } catch (e: AuthException) {
            unauthorized(e)
        } catch (e: JwtException) {
            unauthorized(e)
        } catch (e: Oauth2Exception) {
            unauthorized(e)
        } catch (e: EgressException) {
            handleError(request, "POST", e, responseContentType)
        } finally {
            MDC.clear()
        }
    }

    protected abstract fun checkIngressAuth(request: HttpServletRequest): String

    protected abstract fun provideHeaderAuth(
        request: HttpServletRequest,
        headers: TreeMap<String, String>,
        serviceUserId: Int
    )

    protected abstract fun provideBodyAuth(body: String): String

    protected open fun metricDetail(request: HttpServletRequest): String = request.requestURI

    private fun getEgressHeaders(request: HttpServletRequest, defaultServiceUserId: Int): TreeMap<String, String> {
        val egressHeaders = TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER)
        request.headerNames.toList().forEach { copyHeader(request, it, egressHeaders) }
        provideHeaderAuth(request, egressHeaders, serviceUserId = serviceUserId(request) ?: defaultServiceUserId)
        addCallIdHeader(egressHeaders)
        return egressHeaders
    }

    private fun getResponseContentType(request: HttpServletRequest): HttpHeaders {
        val requestContentType: String? = request.getHeader(HttpHeaders.CONTENT_TYPE)

        return if (requestContentType?.startsWith(MediaType.TEXT_XML_VALUE) == true)
            xmlContentType else jsonContentType
    }

    private fun addCallIdHeader(headers: TreeMap<String, String>) {
        with(resolveCallId(headers)) {
            MDC.put(CALL_ID_HEADER_NAME_1, this)
            headers[CALL_ID_HEADER_NAME_1] = this
        }
    }

    private fun unauthorized(e: Exception) = unauthorized(e.message)

    private fun unauthorized(message: String?): ResponseEntity<String> {
        log.error("Unauthorized: $message")
        return ResponseEntity("Unauthorized", HttpStatus.UNAUTHORIZED)
    }

    private fun handleError(
        request: HttpServletRequest,
        method: String,
        e: EgressException,
        responseContentType: HttpHeaders
    ): ResponseEntity<String> {
        metric("$method ${metricDetail(request)}", "error")
        log.error(e) { "Failed to $method $egressEndpoint${request.requestURI}: ${e.message} (${e.statusCode})" }
        return ResponseEntity(e.message, responseContentType, statusCode(e))
    }

    private fun resolveCallId(headers: TreeMap<String, String>): String =
        NAV_CALL_ID_HEADER_NAMES
            .asSequence()
            .mapNotNull { headers[it] }
            .firstOrNull { it.isNotEmpty() }
            ?: callIdGenerator.newCallId()

    companion object {
        const val CALL_ID_HEADER_NAME_1 = "Nav-Call-Id"
        const val CONSUMER_TOKEN_HEADER_NAME = "Nav-Consumer-Token"
        private const val SERVICE_USER_ID_HEADER_NAME = "Service-User-Id"
        private const val DEFAULT_SERVICE_USER_ID = 1 // srvpselv
        private val locale = Locale.getDefault()

        private val notRelayedHeaders = listOf(
            HttpHeaders.ACCEPT.lowercase(locale),
            HttpHeaders.AUTHORIZATION.lowercase(locale),
            HttpHeaders.HOST.lowercase(locale),
            HttpHeaders.USER_AGENT.lowercase(locale),
            HttpHeaders.CONTENT_LENGTH.lowercase(locale),
            CONSUMER_TOKEN_HEADER_NAME.lowercase(locale),
            SERVICE_USER_ID_HEADER_NAME.lowercase(locale)
        )

        // NB: No consensus in Nav regarding call ID header name,
        // ref. https://github.com/navikt/k9-formidling/blob/master/app/src/main/kotlin/no/nav/k9/formidling/app/logging/LoggingHjelper.kt
        val NAV_CALL_ID_HEADER_NAMES =
            setOf(
                CALL_ID_HEADER_NAME_1,
                "Nav-CallId",
                "Nav-Callid",
                "X-Correlation-Id")

        private fun copyHeader(request: HttpServletRequest, headerName: String, headers: TreeMap<String, String>) {
            if (notRelayedHeaders.contains(headerName.lowercase(locale))) {
                return
            }

            headers[headerName] = request.getHeader(headerName)
        }

        private fun serviceUserId(request: HttpServletRequest): Int? =
            request.getHeader(SERVICE_USER_ID_HEADER_NAME)?.toIntOrNull()

        private val jsonContentType: HttpHeaders =
            contentTypeHeaders(MediaType.APPLICATION_JSON)

        private val xmlContentType: HttpHeaders =
            contentTypeHeaders(MediaType(MediaType.TEXT_XML, StandardCharsets.UTF_8))

        private fun contentTypeHeaders(mediaType: MediaType) =
            HttpHeaders().apply { contentType = mediaType }

        private fun metric(action: String, status: String) =
            Metrics.counter("request_counter", "action", action, "status", status).increment()

        private fun statusCode(e: EgressException) =
            if (e.statusCode.is4xxClientError)
                e.statusCode
            else
                HttpStatus.BAD_GATEWAY
    }
}
