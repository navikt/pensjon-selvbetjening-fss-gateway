package no.nav.pensjon.selvbetjening.fssgw.tps

import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.nio.charset.StandardCharsets

@Component
class TpsConsumer(@Value("\${tps.endpoint.url}") private val endpoint: String) {

    private val log = LogFactory.getLog(javaClass)

    private val webClient: WebClient = WebClient.builder()
            .defaultHeaders { it.contentType = MediaType(MediaType.TEXT_XML, StandardCharsets.UTF_8) }
            .build()

    fun getPerson(body: String): String = callTps(body, "/nav-cons-pen-pselv-personWeb/sca/PSELVPersonWSEXP")

    fun ping(body: String): String = callTps(body, "/nav-cons-test-getapplicationversionWeb/sca/TESTGetApplicationVersionWSEXP")

    private fun callTps(body: String, path: String): String {
        try {
            return webClient
                    .post()
                    .uri(endpoint + path)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String::class.java)
                    .block()
                    ?: throw TpsException("No data in response from TPS at $endpoint")
        } catch (e: WebClientResponseException) {
            val message = "Failed to access TPS at $endpoint: ${e.message} | Response: ${e.responseBodyAsString}"
            log.error(message, e)
            throw TpsException(message, e)
        } catch (e: RuntimeException) { // e.g. when connection broken
            val message = "Failed to access TPS at $endpoint: ${e.message}"
            log.error(message, e)
            throw TpsException(message, e)
        }
    }
}
