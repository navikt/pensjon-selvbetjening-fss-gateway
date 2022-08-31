package no.nav.pensjon.selvbetjening.fssgw.common

import no.nav.pensjon.selvbetjening.fssgw.tech.web.WebClientPreparer
import org.apache.commons.logging.LogFactory
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException

@Component
class ServiceClient {

    private val log = LogFactory.getLog(javaClass)

    // Use large buffer, since response from kodeverk/Postnummer/koder/betydninger is 600 KB
    // (which is more than the default 262 KB)
    private val webClient: WebClient = WebClientPreparer.largeBufferWebClient()

    fun doGet(uri: String, headers: Map<String, String>): String {
        if (log.isDebugEnabled) {
            log.debug("GET from URI: '$uri'")
        }

        try {
            return webClient
                .get()
                .uri(uri)
                .headers { h -> copyHeaders(headers, h) }
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
                ?: ""
        } catch (e: WebClientResponseException) {
            throw ConsumerException(e.responseBodyAsString, e)
        } catch (e: RuntimeException) { // e.g. when connection broken
            throw ConsumerException("Failed to do GET towards $uri: ${e.message}", e)
        }
    }

    fun doOptions(uri: String, headers: Map<String, String>): String {
        if (log.isDebugEnabled) {
            log.debug("OPTIONS from URI: '$uri'")
        }

        try {
            return webClient
                .options()
                .uri(uri)
                .headers { h -> copyHeaders(headers, h) }
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
                ?: ""
        } catch (e: WebClientResponseException) {
            throw ConsumerException(e.responseBodyAsString, e)
        } catch (e: RuntimeException) { // e.g. when connection broken
            throw ConsumerException("Failed to do OPTIONS towards $uri: ${e.message}", e)
        }
    }

    fun doPost(uri: String, headers: Map<String, String>, body: String): String {
        if (log.isDebugEnabled) {
            log.debug("POST to URI: '$uri'")
        }

        try {
            return webClient
                .post()
                .uri(uri)
                .headers { h -> copyHeaders(headers, h) }
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
                ?: ""
        } catch (e: WebClientResponseException) {
            throw ConsumerException(e.responseBodyAsString, e)
        } catch (e: RuntimeException) { // e.g. when connection broken
            throw ConsumerException("Failed to do POST towards $uri: ${e.message}", e)
        }
    }

    private fun copyHeaders(ingressHeaders: Map<String, String>, egressHeaders: HttpHeaders) {
        ingressHeaders.entries.stream().forEach { (k, v) -> egressHeaders.set(k, v) }
    }
}
