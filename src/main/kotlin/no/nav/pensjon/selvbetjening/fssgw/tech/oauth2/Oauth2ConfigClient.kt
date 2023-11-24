package no.nav.pensjon.selvbetjening.fssgw.tech.oauth2

import mu.KotlinLogging
import no.nav.pensjon.selvbetjening.fssgw.tech.oauth2.dto.Oauth2ConfigDto
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.StsException
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException

class Oauth2ConfigClient(
    private val webClient: WebClient,
    private val configUrl: String) : Oauth2ConfigGetter {

    private val log = KotlinLogging.logger {}
    private var config: Oauth2ConfigDto? = null

    override fun getIssuer(): String = cachedConfig.issuer

    override fun getAuthorizationEndpoint(): String = cachedConfig.authorizationEndpoint

    override fun getTokenEndpoint(): String = cachedConfig.tokenEndpoint

    override fun getJsonWebKeySetUri(): String = cachedConfig.jwksUri

    override fun refresh() {
        config = null
    }

    private val cachedConfig: Oauth2ConfigDto
        get() = config ?: freshConfig.also { config = it }

    private val freshConfig: Oauth2ConfigDto
        get() {
            log.debug { "Retrieving OAuth2 config from $configUrl" }

            try {
                return webClient
                    .get()
                    .uri(configUrl)
                    .retrieve()
                    .bodyToMono(Oauth2ConfigDto::class.java)
                    .block()
                    ?: throw Oauth2Exception("No config in response from well-known OAuth2 endpoint at $configUrl")
            } catch (e: WebClientResponseException) {
                val message =
                    "Failed to acquire OAuth2 config from $configUrl: ${e.message} | Response: ${e.responseBodyAsString}"
                log.error(e) { message }
                throw StsException(message, e)
            } catch (e: RuntimeException) { // e.g. when connection broken
                val message = "Failed to access well-known OAuth2 endpoint $configUrl: ${e.message}"
                log.error(e) { message }
                throw StsException(message, e)
            }
        }
}
