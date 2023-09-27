package no.nav.pensjon.selvbetjening.fssgw.common

import mu.KotlinLogging
import no.nav.pensjon.selvbetjening.fssgw.tech.web.WebClientPreparer
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException

@Component
class ServiceClient {

    private val log = KotlinLogging.logger {}

    // Use large buffer, since response from kodeverk/Postnummer/koder/betydninger is 600 KB
    // (which is more than the default 262 KB)
    private val webClient: WebClient = WebClientPreparer.largeBufferWebClient()

    fun doGet(uri: String, headers: Map<String, String>): String {
        if (log.isDebugEnabled) {
            log.debug { "GET from URI: '$uri'" }
        }

        try {
            return webClient
                .get()
                .uri(uri)
                .headers { copyHeaders(headers, it) }
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
                ?: ""
        } catch (e: WebClientResponseException) {
            throw EgressException(message = e.responseBodyAsString, cause = e)
        }
    }

    fun doOptions(uri: String, headers: Map<String, String>): String {
        if (log.isDebugEnabled) {
            log.debug { "OPTIONS from URI: '$uri'" }
        }

        try {
            return webClient
                .options()
                .uri(uri)
                .headers { copyHeaders(headers, it) }
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
                ?: ""
        } catch (e: WebClientResponseException) {
            throw EgressException(message = e.responseBodyAsString, cause = e)
        }
    }

    fun doPost(uri: String, headers: Map<String, String>, body: String): String {
        if (log.isDebugEnabled) {
            log.debug { "POST to URI: '$uri'" }
        }

        try {
            return webClient
                .post()
                .uri(uri)
                .headers { copyHeaders(headers, it) }
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
                ?: ""
        } catch (e: WebClientResponseException) {
            throw EgressException(message = e.responseBodyAsString, cause = e)
        }
    }

    private fun copyHeaders(ingressHeaders: Map<String, String>, egressHeaders: HttpHeaders) {
        ingressHeaders.entries.stream().forEach { (k, v) -> egressHeaders.set(k, v) }
    }
}
