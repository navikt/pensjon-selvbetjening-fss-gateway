package no.nav.pensjon.selvbetjening.fssgw.skjerm

import no.nav.pensjon.selvbetjening.fssgw.common.ConsumerException
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.netty.http.client.HttpClient

@Component
class SkjermingConsumer(@Value("\${skjerming.url}") private val endpoint: String) {

    private val log = LogFactory.getLog(javaClass)
    private val webClient = webClient()

    fun isSkjermet(urlSuffix: String, headers: HashMap<String, String>): String {
        try {
            return webClient
                .get()
                .uri(endpoint.plus("/").plus(urlSuffix))
                .headers { h -> copyHeaders(headers, h) }
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
                ?: throw ConsumerException("No data in response from Skjermede-personer-PIP")
        } catch (e: WebClientResponseException) {
            val message = "Failed to access PEN at $endpoint: ${e.message} | Response: ${e.responseBodyAsString}"
            log.error(message, e)
            throw ConsumerException(message, e)
        } catch (e: RuntimeException) { // e.g. when connection broken
            val message = "Failed to access PEN at $endpoint: ${e.message}"
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
}
