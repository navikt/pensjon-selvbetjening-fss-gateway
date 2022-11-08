package no.nav.pensjon.selvbetjening.fssgw.common

import io.jsonwebtoken.JwtException
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import org.springframework.http.HttpHeaders
import org.springframework.util.StringUtils
import javax.servlet.http.HttpServletRequest

abstract class TokenProtectedController(
    private val tokenValidator: JwsValidator,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    egressEndpoint: String) : ControllerBase(serviceClient, callIdGenerator, egressEndpoint) {

    override fun checkIngressAuth(request: HttpServletRequest) {
        val auth: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        val accessToken: String = auth?.substring(AUTH_TYPE.length + 1) ?: ""

        if (!StringUtils.hasText(accessToken)) {
            throw JwtException("Missing access token")
        }

        tokenValidator.validate(accessToken)
    }

    companion object {
        private const val AUTH_TYPE = "Bearer"
    }
}
