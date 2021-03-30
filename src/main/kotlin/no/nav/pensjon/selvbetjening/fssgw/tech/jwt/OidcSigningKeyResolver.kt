package no.nav.pensjon.selvbetjening.fssgw.tech.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwsHeader
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.SigningKeyResolverAdapter
import no.nav.pensjon.selvbetjening.fssgw.tech.rsa.JsonWebKey
import org.springframework.stereotype.Component
import java.security.Key

/**
 * Obtains the public key required to validate a signed JWT.
 * Keys are cached, so that the source endpoint is not unnecessarily accessed.
 */
@Component
class OidcSigningKeyResolver(val keyDataGetter: KeyDataGetter) : SigningKeyResolverAdapter() {

    private val cachedKeysById: MutableMap<String, Key> = HashMap()

    override fun resolveSigningKey(header: JwsHeader<*>, claims: Claims): Key {
        val keyId = header.keyId
        return cachedKeysById.computeIfAbsent(keyId) { getKey(keyId) }
    }

    private fun getKey(keyId: String): Key {
        val keyData: JwtKeyDto = getKeyData(keyId)
        return JsonWebKey(keyData.id, keyData.use, keyData.modulus, keyData.exponent).getRsaPublicKey()
    }

    private fun getKeyData(keyId: String): JwtKeyDto =
            try {
                keyDataGetter.getKeyData(keyId)
            } catch (e: SigningKeyException) {
                retry(keyId)
            }

    private fun retry(keyId: String): JwtKeyDto {
        keyDataGetter.refresh()

        return try {
            keyDataGetter.getKeyData(keyId)
        } catch (e: SigningKeyException) {
            throw JwtException("Failed to get certificate for key ID $keyId", e)
        }
    }
}
