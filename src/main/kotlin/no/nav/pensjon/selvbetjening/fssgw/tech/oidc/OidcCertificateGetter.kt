package no.nav.pensjon.selvbetjening.fssgw.tech.oidc

import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.CertificateGetter
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwtKeyDto
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwtKeysDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.security.cert.CertificateException

@Component
class OidcCertificateGetter(val oidcConfigGetter: OidcConfigGetter) : CertificateGetter {

    private val webClient: WebClient = WebClient.create()
    private val log = LoggerFactory.getLogger(javaClass)
    private var keys: JwtKeysDto? = null

    override fun getCertificate(id: String): String {
        return cache.keys
                .stream()
                .filter { key -> match(key, id) }
                .findFirst()
                .map { key -> key.x509CertificateChain[0] }
                .orElseThrow { noCertificateFound(id) }
    }

    override fun refresh() {
        keys = null
        oidcConfigGetter.refresh()
    }

    private val cache: JwtKeysDto
        get() = keys ?: freshKeys.also { keys = it }

    private val freshKeys: JwtKeysDto
        get() {
            log.debug("Retrieving OIDC config")
            val uri = oidcConfigGetter.getJsonWebKeySetUri()

            return try {
                webClient
                        .get()
                        .uri(uri)
                        .retrieve()
                        .bodyToMono(JwtKeysDto::class.java)
                        .block()
                        ?: throw OidcException("No JWT keys in response from OIDC key endpoint $uri")
            } catch (e: WebClientResponseException) {
                val message = "Failed to acquire JWT keys from $uri: ${e.message} | Response: ${e.responseBodyAsString}"
                log.error(message, e)
                throw OidcException(message, e)
            } catch (e: RuntimeException) { // e.g. when connection broken
                val message = "Failed to acquire JWT keys from $uri: ${e.message}"
                log.error(message, e)
                throw OidcException(message, e)
            }
        }

    companion object {
        private fun match(key: JwtKeyDto, keyId: String?): Boolean =
                keyId?.equals(key.id) ?: throw IllegalArgumentException("Illegal keyId value: null")

        private fun noCertificateFound(keyId: String?): CertificateException =
                CertificateException(String.format("No certificate found for key ID '%s'", keyId))
    }
}
