package no.nav.pensjon.selvbetjening.fssgw.ereg

import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.util.*

@Component
class EregConsumer(@Value("\${ereg.url}") private val endpoint: String) {
    private val log = LogFactory.getLog(javaClass)
    private val webClient: WebClient = WebClient.create()

    fun getOrganisasjonNoekkelinfo(navCallId: String?, organisasjonsnummer: String): String {
        val correlationId = navCallId ?: UUID.randomUUID().toString()
        log.info("Calling Ereg with correlation ID '$correlationId'")

        try {
            return webClient
                    .get()
                    .uri("$endpoint/v1/organisasjon/$organisasjonsnummer/noekkelinfo")
                    .header(NAV_CALL_ID, correlationId)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .bodyToMono(String::class.java)
                    .block()
                    ?: throw EregException("No data in response from Ereg at $endpoint")
        } catch (e: WebClientResponseException) {
            val message = "Failed to access Ereg at $endpoint: ${e.message} | Response: ${e.responseBodyAsString}"
            log.error(message, e)
            throw EregException(message, e)
        } catch (e: RuntimeException) { // e.g. when connection broken
            val message = "Failed to access Ereg at $endpoint: ${e.message}"
            log.error(message, e)
            throw EregException(message, e)
        }
    }

    companion object EregHttpHeaders {
        private const val NAV_CALL_ID = "Nav-Call-Id"
    }
}