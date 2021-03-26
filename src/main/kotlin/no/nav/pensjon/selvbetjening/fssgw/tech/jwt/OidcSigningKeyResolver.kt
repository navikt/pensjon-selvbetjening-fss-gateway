package no.nav.pensjon.selvbetjening.fssgw.tech.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwsHeader
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.SigningKeyResolverAdapter
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream
import java.security.Key
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.xml.bind.DatatypeConverter

/**
 * Obtains the public key required to validate a signed JWT.
 * Keys are cached, so that the source endpoint is not unnecessarily accessed.
 */
@Component
class OidcSigningKeyResolver(val certificateGetter: CertificateGetter) : SigningKeyResolverAdapter() {

    private val cachedKeysById: MutableMap<String, Key> = HashMap()

    override fun resolveSigningKey(header: JwsHeader<*>, claims: Claims): Key {
        val keyId = header.keyId
        return cachedKeysById.getOrDefault(keyId, getKey(keyId))
    }

    private fun getKey(keyId: String): Key {
        val certificateBytes = DatatypeConverter.parseBase64Binary(getX509Certificate(keyId))

        ByteArrayInputStream(certificateBytes).use { inStream ->
            val key = generateCertificate(inStream).publicKey
            cachedKeysById[keyId] = key
            return key
        }
    }

    private fun getX509Certificate(keyId: String): String =
            try {
                certificateGetter.getCertificate(keyId)
            } catch (e: CertificateException) {
                retry(keyId)
            }

    private fun retry(keyId: String): String {
        certificateGetter.refresh()

        return try {
            certificateGetter.getCertificate(keyId)
        } catch (e: CertificateException) {
            throw JwtException("Failed to get certificate for key ID $keyId", e)
        }
    }

    companion object {
        private const val CERTIFICATE_TYPE = "X.509"

        private fun generateCertificate(inStream: ByteArrayInputStream): X509Certificate =
                try {
                    CertificateFactory
                            .getInstance(CERTIFICATE_TYPE)
                            .generateCertificate(inStream) as X509Certificate
                } catch (e: CertificateException) {
                    throw JwtException("Failed to generate certificate: ${e.message}", e)
                }
    }
}
