package no.nav.pensjon.selvbetjening.fssgw.common

import no.nav.pensjon.selvbetjening.fssgw.tech.web.WebClientPreparer
import org.apache.commons.logging.LogFactory
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.util.*

@Component
class ServiceClient {

    private val log = LogFactory.getLog(javaClass)

    // Use large buffer, since response from kodeverk/Postnummer/koder/betydninger is 600 KB
    // (which is more than the default 262 KB)
    private val webClient: WebClient = WebClientPreparer.largeBufferWebClient()

    fun callService(uri: String, headers: TreeMap<String, String>): String {
        if (log.isDebugEnabled) {
            log.debug("URI: '$uri'")
            log(headers)
        }

        try {
            return webClient
                .get()
                .uri(uri)
                .headers { h -> copyHeaders(headers, h) }
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
                ?: throw ConsumerException("No data in response from service")
        } catch (e: WebClientResponseException) {
            val message = "Failed to access service at $uri: ${e.message} | Response: ${e.responseBodyAsString}"
            log.error(message, e)
            throw ConsumerException(message, e)
        } catch (e: RuntimeException) { // e.g. when connection broken
            val message = "Failed to access service at $uri: ${e.message}"
            log.error(message, e)
            throw ConsumerException(message, e)
        }
    }

    private fun copyHeaders(ingressHeaders: TreeMap<String, String>, egressHeaders: HttpHeaders) {
        ingressHeaders.entries.stream().forEach { (k, v) -> egressHeaders.set(k, v) }
    }

    private fun log(headers: TreeMap<String, String>) {
        log.debug("Egress headers:")
        headers.entries.stream().forEach { (k, v) -> log.debug("$k: $v") }
    }
}
