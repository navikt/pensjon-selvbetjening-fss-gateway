package no.nav.pensjon.selvbetjening.fssgw.tech.rsa

import com.nimbusds.jose.jwk.KeyUse
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.util.Base64URL
import java.security.interfaces.RSAPublicKey

class JsonWebKey(private val keyId: String,
                 private val use: String,
                 private val modulus: String,
                 private val exponent: String) {

    fun getKeyId(): String = keyId

    fun getRsaPublicKey(): RSAPublicKey = newRsaKey.toRSAPublicKey()

    private val newRsaKey
        get() = RSAKey(
                Base64URL(modulus),
                Base64URL(exponent),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                KeyUse.parse(use),
                null,
                null,
                keyId,
                null,
                null,
                null,
                null,
                null)
}
