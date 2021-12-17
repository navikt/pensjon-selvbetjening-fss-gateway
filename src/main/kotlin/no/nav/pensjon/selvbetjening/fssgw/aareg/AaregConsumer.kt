package no.nav.pensjon.selvbetjening.fssgw.aareg

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
class AaregConsumer(@Value("\${aareg.url}") private val endpoint: String,
                    private val serviceUserTokenGetter: ServiceTokenGetter) {
    private val log = LogFactory.getLog(javaClass)
    private val webClient: WebClient = WebClient.create()

    fun getArbeidsgivere(navCallId: String?, personIdent: String): String {
        val auth = auth()
        val correlationId = navCallId ?: UUID.randomUUID().toString()
        log.info("Calling Aareg with correlation ID '$correlationId'")

        try {
            return webClient
                    .get()
                    .uri("$endpoint/aareg-services/api/v1/arbeidstaker/arbeidsforhold")
                    .header(HttpHeaders.AUTHORIZATION, auth)
                    .header(CONSUMER_TOKEN, auth)
                    .header(NAV_CALL_ID, correlationId)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .header(PERSON_IDENT, personIdent)
                    .retrieve()
                    .bodyToMono(String::class.java)
                    .block()
                    ?: throw AaregException("No data in response from Aareg at $endpoint")
        } catch (e: WebClientResponseException) {
            val message = "Failed to access Aareg at $endpoint: ${e.message} | Response: ${e.responseBodyAsString}"
            log.error(message, e)
            throw AaregException(message, e)
        } catch (e: RuntimeException) { // e.g. when connection broken
            val message = "Failed to access Aareg at $endpoint: ${e.message}"
            log.error(message, e)
            throw AaregException(message, e)
        }
    }

    private fun auth(): String {
        val serviceUserToken = serviceUserTokenGetter.getServiceUserToken().accessToken
        return "Bearer $serviceUserToken"
    }

    companion object AaregHttpHeaders {
        private const val CONSUMER_TOKEN = "Nav-Consumer-Token"
        private const val PERSON_IDENT = "Nav-Personident"
        private const val NAV_CALL_ID = "Nav-Call-Id"
    }
}
