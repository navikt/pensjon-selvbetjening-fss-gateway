package no.nav.pensjon.selvbetjening.fssgw.tech.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SigningKeyResolver
import no.nav.pensjon.selvbetjening.fssgw.tech.oauth2.MultiIssuerSupport
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Validator of JSON Web Signature (JWS) strings.
 */
@Component
class JwsValidator(
    private val multiIssuerSupport: MultiIssuerSupport,
    private val signingKeyResolver: SigningKeyResolver) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun validate(jwsString: String): Claims {
        log.debug("Validating '{}'", jwsString)

        val claims = Jwts.parser()
            .setSigningKeyResolver(signingKeyResolver)
            .build()
            .parseClaimsJws(jwsString)
            .body

        validate(claims)
        return claims
    }

    private fun validate(claims: Claims) {
        val audiences: Set<String> = claims.audience
        val acceptedAudience = multiIssuerSupport.getOauth2HandlerForIssuer(claims.issuer).acceptedAudience

        if (log.isDebugEnabled) {
            val roles = claims["roles"]

            if (roles is List<*> && roles.contains("access_as_application")) {
                log.debug("Access as application")
            }
        }

        if (audiences.contains(acceptedAudience)) {
            val message = "Invalid audience '$audiences'"
            log.error(message)
            throw JwtException(message)
        }
    }
}
