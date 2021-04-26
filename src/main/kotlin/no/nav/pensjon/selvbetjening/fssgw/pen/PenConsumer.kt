package no.nav.pensjon.selvbetjening.fssgw.pen

import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException

@Component
class PenConsumer(@Value("\${pen.endpoint.url}") private val endpoint: String,
                  private val serviceUserTokenGetter: ServiceTokenGetter) {

    private val log = LogFactory.getLog(javaClass)
    private val webClient: WebClient = WebClient.create()

    fun callPen(body: String, callId: String?, pid: String): String {
        val auth = auth()

        try {
            return webClient
                    .post()
                    .uri(endpoint)
                    .header(HttpHeaders.AUTHORIZATION, auth)
                    .header("pid", pid)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
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

    private fun auth(): String {
        val serviceUserToken = serviceUserTokenGetter.getServiceUserToken().accessToken
        return "Bearer $serviceUserToken"
    }
}
