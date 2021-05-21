package no.nav.pensjon.selvbetjening.fssgw.tech.oauth2

import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwtKeyDto
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwtKeysDto
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.KeyDataGetter
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.SigningKeyException
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException

class Oauth2KeyDataGetter(private val webClient: WebClient,
                          private val configGetter: Oauth2ConfigGetter) : KeyDataGetter {

    private val log = LoggerFactory.getLogger(javaClass)
    private var keys: JwtKeysDto? = null

    override fun getKeyData(id: String): JwtKeyDto {
        return cache.keys
                .stream()
                .filter { key -> match(key, id) }
                .findFirst()
                .orElseThrow { noKeyDataFound(id) }
    }

    override fun refresh() {
        keys = null
        configGetter.refresh()
    }

    private val cache: JwtKeysDto
        get() = keys ?: freshKeys.also { keys = it }

    private val freshKeys: JwtKeysDto
        get() {
            log.debug("Retrieving OAuth2 config")
            val uri = configGetter.getJsonWebKeySetUri()

            return try {
                webClient
                        .get()
                        .uri(uri)
                        .retrieve()
                        .bodyToMono(JwtKeysDto::class.java)
                        .block()
                        ?: throw Oauth2Exception("No JWT keys in response from OAuth2 key endpoint $uri")
            } catch (e: WebClientResponseException) {
                val message = "Failed to acquire JWT keys from $uri: ${e.message} | Response: ${e.responseBodyAsString}"
                log.error(message, e)
                throw Oauth2Exception(message, e)
            } catch (e: RuntimeException) { // e.g. when connection broken
                val message = "Failed to acquire JWT keys from $uri: ${e.message}"
                log.error(message, e)
                throw Oauth2Exception(message, e)
            }
        }

    companion object {
        private fun match(key: JwtKeyDto, keyId: String?): Boolean =
                keyId?.equals(key.id) ?: throw IllegalArgumentException("Illegal keyId value: null")

        private fun noKeyDataFound(keyId: String?): SigningKeyException =
                SigningKeyException("No key data found for key ID '$keyId'")
    }
}
