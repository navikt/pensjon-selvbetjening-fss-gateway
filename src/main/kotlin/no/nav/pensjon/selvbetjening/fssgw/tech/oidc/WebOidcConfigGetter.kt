package no.nav.pensjon.selvbetjening.fssgw.tech.oidc

import no.nav.pensjon.selvbetjening.fssgw.tech.sts.StsException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException

@Component
class WebOidcConfigGetter(@Value("\${openid.well-known-url}") private val configUrl: String) : OidcConfigGetter {

    private val webClient: WebClient = WebClient.create()
    private val log = LoggerFactory.getLogger(javaClass)
    private var config: OidcConfigDto? = null

    override fun getIssuer(): String = cachedConfig.issuer

    override fun getAuthorizationEndpoint(): String = cachedConfig.authorizationEndpoint

    override fun getTokenEndpoint(): String = cachedConfig.tokenEndpoint

    override fun getJsonWebKeySetUri(): String = cachedConfig.jwksUri

    override fun refresh() {
        config = null
    }

    private val cachedConfig: OidcConfigDto
        get() = config ?: freshConfig.also { config = it }

    private val freshConfig: OidcConfigDto
        get() {
            log.debug("Retrieving OIDC config")

            return try {
                webClient
                        .get()
                        .uri(configUrl)
                        .retrieve()
                        .bodyToMono(OidcConfigDto::class.java)
                        .block()
                        ?: throw OidcException("No config in response from well-known OIDC endpoint")
            } catch (e: WebClientResponseException) {
                val message = "Failed to acquire OIDC config from $configUrl: ${e.message} | Response: ${e.responseBodyAsString}"
                log.error(message, e)
                throw StsException(message, e)
            } catch (e: RuntimeException) { // e.g. when connection broken
                val message = "Failed to access well-known OIDC endpoint $configUrl: ${e.message}"
                log.error(message, e)
                throw StsException(message, e)
            }
        }
}
