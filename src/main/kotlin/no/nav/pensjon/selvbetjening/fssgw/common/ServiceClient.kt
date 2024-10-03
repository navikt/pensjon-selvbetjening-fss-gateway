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

    // Use large buffer, since JournalWSEXP calls sometimes exceed 10 MB
    // (which is more than the default 262 KB)
    private val webClient: WebClient = WebClientPreparer.largeBufferWebClient()
    private val externalWebClient: WebClient = WebClientPreparer.externalWebClient()

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

    fun doPost(uri: String, headers: Map<String, String>, body: String, externalCall: Boolean = false): String {
        if (log.isDebugEnabled) {
            log.debug { "POST to URI: '$uri'" }
        }
        val client = if (externalCall) externalWebClient else webClient

        try {
            return client
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
