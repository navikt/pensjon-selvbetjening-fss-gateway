package no.nav.pensjon.selvbetjening.fssgw.dkif

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
class DkifConsumer(@Value("\${dkif.url}") private val endpoint: String,
                   private val serviceUserTokenGetter: ServiceTokenGetter) {
    private val log = LogFactory.getLog(javaClass)
    private val webClient: WebClient = WebClient.create()

    fun getKontaktinfo(callId: String?, consumerId : String, inkluderSikkerDigitalPost : Boolean, personIdent: String): String {
        val auth = auth()
        val correlationId = callId ?: UUID.randomUUID().toString()
        log.info("Calling Journalforing with correlation ID '$correlationId'")

        try {
            return webClient
                    .get()
                    .uri("$endpoint?inkluderSikkerDigitalPost=$inkluderSikkerDigitalPost")
                    .header(HttpHeaders.AUTHORIZATION, auth)
                    .header(CONSUMER_TOKEN, auth)
                    .header(NAV_CALL_ID, callId)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .header(CONSUMER_ID, consumerId)
                    .header(PERSON_IDENTER, personIdent)
                    .retrieve()
                    .bodyToMono(String::class.java)
                    .block()
                    ?: throw DkifException("No data in response from Dkif at $endpoint")
        } catch (e: WebClientResponseException) {
            val message = "Failed to access Dkif at $endpoint: ${e.message} | Response: ${e.responseBodyAsString}"
            log.error(message, e)
            throw DkifException(message, e)
        } catch (e: RuntimeException) { // e.g. when connection broken
            val message = "Failed to access Dkif at $endpoint: ${e.message}"
            log.error(message, e)
            throw DkifException(message, e)
        }
    }

    private fun auth(): String {
        val serviceUserToken = serviceUserTokenGetter.getServiceUserToken().accessToken
        return "Bearer $serviceUserToken"
    }

    companion object DkifHttpHeaders {
        private const val CONSUMER_TOKEN = "Nav-Consumer-Token"
        private const val CONSUMER_ID = "Nav-Consumer-Id"
        private const val PERSON_IDENTER = "Nav-Personidenter"
        private const val NAV_CALL_ID = "Nav-Call-Id"
    }
}