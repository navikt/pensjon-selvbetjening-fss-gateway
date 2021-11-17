package no.nav.pensjon.selvbetjening.fssgw.kodeverk

import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException

@Component
class KodeverkConsumer(@Value("\${kodeverk.url}") private val endpoint: String) {

    private val log = LogFactory.getLog(javaClass)
    private val webClient: WebClient = WebClient.create()

    fun getBetydningerForPostnummer(callId: String?, consumerId: String?, sprak: String): String {
        val url  = "$endpoint/Postnummer/koder/betydninger?spraak=$sprak"
        log.info("Calling Journalforing with correlation ID '$callId'")

        try {
            return webClient
                    .get()
                    .uri(url)
                    .header(NAV_CALL_ID, callId)
                    .header(NAV_CONSUMER_ID, consumerId)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .bodyToMono(String::class.java)
                    .block()
                    ?: throw KodeverkException("No data in response from Kodeverk at $url")
        } catch (e: WebClientResponseException) {
            val message = "Failed to access Kodeverk at $url: ${e.message} | Response: ${e.responseBodyAsString}"
            log.error(message, e)
            throw KodeverkException(message, e)
        } catch (e: RuntimeException) { // e.g. when connection broken
            val message = "Failed to access Kodeverk at $url: ${e.message}"
            log.error(message, e)
            throw KodeverkException(message, e)
        }
    }

    companion object JournalforingHttpHeaders {
        private const val NAV_CALL_ID = "Nav-Call-Id"
        private const val NAV_CONSUMER_ID = "Nav-Consumer-Id"
    }
}
