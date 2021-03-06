package no.nav.pensjon.selvbetjening.fssgw.pen

import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.util.*

@Component
class PenConsumer(@Value("\${pen.endpoint.url}") private val endpoint: String,
                  private val serviceUserTokenGetter: ServiceTokenGetter) {

    private val log = LogFactory.getLog(javaClass)
    private val webClient: WebClient = WebClient.create()

    fun callPen(urlSuffix: String, body: String, callId: String?, pid: String, fomDato: String): String {
        return callPenClient(urlSuffix, body, callId, pid, fomDato)
    }

    fun callPen(urlSuffix: String, body: String, callId: String?, pid: String): String {
        return callPenClient(urlSuffix, body, callId, pid, null)
    }

    private fun callPenClient(urlSuffix: String, body: String, callId: String?, pid: String, fomDato: String?): String {
        val correlationId = callId ?: UUID.randomUUID().toString()
        try {
            return webClient
                    .post()
                    .uri(endpoint.plus(urlSuffix))
                    .headers {
                        it.setBearerAuth(auth)
                        it.contentType = MediaType.APPLICATION_JSON
                        it.accept = listOf(MediaType.APPLICATION_JSON)
                        it.set("Nav-Call-Id", correlationId)
                        it.set("pid", pid)
                        it.set("fnr", pid)
                        it.set("fomDato", fomDato)
                    }
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String::class.java)
                    .block()
                    ?: throw PenException("No data in response from PEN at $endpoint")
        } catch (e: WebClientResponseException) {
            val message = "Failed to access PEN at $endpoint: ${e.message} | Response: ${e.responseBodyAsString}"
            log.error(message, e)
            throw PenException(message, e)
        } catch (e: RuntimeException) { // e.g. when connection broken
            val message = "Failed to access PEN at $endpoint: ${e.message}"
            log.error(message, e)
            throw PenException(message, e)
        }
    }

    fun ping(urlSuffix: String): String {
        try {
            return webClient
                    .get()
                    .uri(endpoint.plus(urlSuffix))
                    .headers { it.setBearerAuth(auth) }
                    .retrieve()
                    .bodyToMono(String::class.java)
                    .block()
                    ?: throw PenException("No data in response from PEN at $endpoint")
        } catch (e: WebClientResponseException) {
            val message = "Failed to access PEN at $endpoint: ${e.message} | Response: ${e.responseBodyAsString}"
            log.error(message, e)
            throw PenException(message, e)
        } catch (e: RuntimeException) { // e.g. when connection broken
            val message = "Failed to access PEN at $endpoint: ${e.message}"
            log.error(message, e)
            throw PenException(message, e)
        }
    }

    private val auth: String
        get() = serviceUserTokenGetter.getServiceUserToken().accessToken
}
