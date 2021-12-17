package no.nav.pensjon.selvbetjening.fssgw.common

import org.apache.commons.logging.LogFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.netty.http.client.HttpClient

@Component
class ServiceClient {

    private val log = LogFactory.getLog(javaClass)
    private val webClient = webClient()

    fun callService(uri: String, headers: HashMap<String, String>): String {
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

    private fun webClient(): WebClient {
        val httpClient = HttpClient.create().wiretap(true)

        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .build()
    }

    private fun copyHeaders(ingressHeaders: HashMap<String, String>, egressHeaders: HttpHeaders) {
        ingressHeaders.entries.stream().forEach { (k, v) -> egressHeaders.set(k, v) }
    }

    private fun log(headers: HashMap<String, String>) {
        log.debug("Egress headers:")
        headers.entries.stream().forEach { (k, v) -> log.debug("$k: $v") }
    }
}
