package no.nav.pensjon.selvbetjening.fssgw.tech.oauth2

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwsHeader
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.SigningKeyResolverAdapter
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwtKeyDto
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.KeyDataGetter
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.SigningKeyException
import no.nav.pensjon.selvbetjening.fssgw.tech.rsa.JsonWebKey
import org.springframework.stereotype.Component
import java.security.Key

/**
 * Obtains the public key required to validate a signed JWT.
 * Keys are cached, so that the source endpoint is not unnecessarily accessed.
 */
@Component
class Oauth2SigningKeyResolver(private val multiIssuerSupport: MultiIssuerSupport) : SigningKeyResolverAdapter() {

    private val cachedKeysById: MutableMap<String, Key> = HashMap()

    override fun resolveSigningKey(header: JwsHeader<*>, claims: Claims): Key {
        val keyId = header.keyId

        return cachedKeysById.computeIfAbsent(keyId) {
            getKey(keyId, multiIssuerSupport.getOauth2HandlerForIssuer(claims.issuer).keyDataGetter)
        }
    }

    private fun getKey(keyId: String, keyDataGetter: KeyDataGetter): Key {
        val keyData = getKeyData(keyId, keyDataGetter)
        val jwk = JsonWebKey(keyData.id, keyData.use, keyData.modulus, keyData.exponent)
        return jwk.getRsaPublicKey()
    }

    private fun getKeyData(keyId: String, keyDataGetter: KeyDataGetter): JwtKeyDto =
            try {
                keyDataGetter.getKeyData(keyId)
            } catch (e: SigningKeyException) {
                retry(keyId, keyDataGetter)
            }

    private fun retry(keyId: String, keyDataGetter: KeyDataGetter): JwtKeyDto {
        keyDataGetter.refresh()

        return try {
            keyDataGetter.getKeyData(keyId)
        } catch (e: SigningKeyException) {
            throw JwtException("Failed to get key with ID $keyId", e)
        }
    }
}
