package no.nav.pensjon.selvbetjening.fssgw.pdl

import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.util.*

private const val PDL_THEME = "PEN"

@Component
class PdlConsumer(@Value("\${pdl.endpoint.url}") private val endpoint: String,
                  private val serviceUserTokenGetter: ServiceTokenGetter) {

    private val log = LogFactory.getLog(javaClass)
    private val webClient: WebClient = WebClient.create()

    fun callPdl(body: String, callId: String?): String {
        val auth = auth()
        val correlationId = callId ?: UUID.randomUUID().toString()
        log.info("Calling PDL with correlation ID '$correlationId'")

        try {
            return webClient
                    .post()
                    .uri(endpoint)
                    .header(HttpHeaders.AUTHORIZATION, auth)
                    .header(CONSUMER_TOKEN, auth)
                    .header(CORRELATION_ID, correlationId)
                    .header(THEME, PDL_THEME)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String::class.java)
                    .block()
                    ?: throw PdlException("No data in response from PDL at $endpoint")
        } catch (e: WebClientResponseException) {
            val message = "Failed to access PDL at $endpoint: ${e.message} | Response: ${e.responseBodyAsString}"
            log.error(message, e)
            throw PdlException(message, e)
        } catch (e: RuntimeException) { // e.g. when connection broken
            val message = "Failed to access PDL at $endpoint: ${e.message}"
            log.error(message, e)
            throw PdlException(message, e)
        }
    }

    fun ping() {
        try {
            webClient
                    .options()
                    .uri(endpoint)
                    .header(THEME, PDL_THEME)
                    .retrieve()
                    .toBodilessEntity()
                    .block()
        } catch (e: WebClientResponseException) {
            val message = "Failed to ping PDL at $endpoint: ${e.message} | Response: ${e.responseBodyAsString}"
            log.error(message, e)
            throw PdlException(message, e)
        } catch (e: RuntimeException) { // e.g. when connection broken
            val message = "Failed to ping PDL at $endpoint: ${e.message}"
            log.error(message, e)
            throw PdlException(message, e)
        }
    }

    private fun auth(): String {
        val serviceUserToken = serviceUserTokenGetter.getServiceUserToken().accessToken
        return "Bearer $serviceUserToken"
    }

    companion object PdlHttpHeaders{
        private const val CONSUMER_TOKEN = "Nav-Consumer-Token"
        private const val CORRELATION_ID = "X-Correlation-ID"
        private const val THEME = "Tema"
    }
}
