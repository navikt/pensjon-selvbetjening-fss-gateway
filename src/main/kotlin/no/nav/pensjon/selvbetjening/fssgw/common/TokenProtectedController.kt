package no.nav.pensjon.selvbetjening.fssgw.common

import io.jsonwebtoken.JwtException
import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import org.springframework.http.HttpHeaders
import org.springframework.util.StringUtils.hasText

abstract class TokenProtectedController(
    private val tokenValidator: JwsValidator,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    egressEndpoint: String) : ControllerBase(serviceClient, callIdGenerator, egressEndpoint) {

    override fun checkIngressAuth(request: HttpServletRequest): String {
        val auth: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        val accessToken: String = auth?.substring(AUTH_TYPE.length + 1) ?: ""

        if (!hasText(accessToken)) {
            throw JwtException("Missing access token")
        }

        val claims = tokenValidator.validate(accessToken)
        return claims["azp"] as? String ?: "anonymous"
    }

    companion object {
        private const val AUTH_TYPE = "Bearer"
    }
}
