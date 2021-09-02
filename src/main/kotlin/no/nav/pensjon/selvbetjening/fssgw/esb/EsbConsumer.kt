package no.nav.pensjon.selvbetjening.fssgw.esb

import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.nio.charset.StandardCharsets

@Component
class EsbConsumer(@Value("\${esb.endpoint.url}") private val endpoint: String) {

    private val log = LogFactory.getLog(javaClass)

    private val webClient: WebClient = WebClient.builder()
        .defaultHeaders { it.contentType = MediaType(MediaType.TEXT_XML, StandardCharsets.UTF_8) }
        .build()

    fun callEsb(path: String, body: String): String {
        try {
            return webClient
                .post()
                .uri(endpoint + path)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
                ?: throw EsbException("No data in response from ESB at $endpoint")
        } catch (e: WebClientResponseException) {
            val message = "Failed to access ESB at $endpoint: ${e.message} | Response: ${e.responseBodyAsString}"
            log.error(message, e)
            throw EsbException(message, e)
        } catch (e: RuntimeException) { // e.g. when connection broken
            val message = "Failed to access ESB at $endpoint: ${e.message}"
            log.error(message, e)
            throw EsbException(message, e)
        }
    }
}
