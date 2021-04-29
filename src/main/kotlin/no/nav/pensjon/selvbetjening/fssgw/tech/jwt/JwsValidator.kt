package no.nav.pensjon.selvbetjening.fssgw.tech.jwt

import io.jsonwebtoken.*
import no.nav.pensjon.selvbetjening.fssgw.tech.oidc.OidcConfigGetter
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Validator of JSON Web Signature (JWS) strings.
 */
@Component
class JwsValidator(private val oidcConfigGetter: OidcConfigGetter,
                   private val signingKeyResolver: SigningKeyResolver,
                   @Value("\${oauth2.audience}") private val acceptedAudience: String) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun validate(jwsString: String): Claims {
        log.info("Validating '{}'", jwsString)

        val jws = Jwts.parserBuilder()
                .setSigningKeyResolver(signingKeyResolver)
                .build()
                .parseClaimsJws(jwsString)

        validate(jws.body)
        return jws.body
    }

    private fun validate(claims: Claims) {
        val audience = claims.audience
        val issuer = claims.issuer

        if (acceptedAudience != audience) {
            val message = "Invalid audience '$audience'"
            log.error(message)
            throw JwtException(message)
        }

        if (oidcConfigGetter.getIssuer() != issuer) {
            val message = "Invalid issuer '$issuer'"
            log.error(message)
            throw JwtException(message)
        }
    }
}
