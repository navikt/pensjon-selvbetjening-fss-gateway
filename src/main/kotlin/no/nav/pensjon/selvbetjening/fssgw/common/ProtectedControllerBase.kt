package no.nav.pensjon.selvbetjening.fssgw.common

import io.jsonwebtoken.JwtException
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.springframework.http.HttpHeaders
import org.springframework.util.StringUtils.hasText
import java.util.*
import javax.servlet.http.HttpServletRequest

abstract class ProtectedControllerBase(
    private val jwsValidator: JwsValidator,
    private val egressTokenGetter: ServiceTokenGetter,
    serviceClient: ServiceClient,
    egressEndpoint: String) : ControllerBase(serviceClient, egressEndpoint) {

    private val authType = "Bearer"

    protected abstract fun egressAuthWaived(): Boolean

    protected abstract fun consumerTokenRequired(): Boolean

    override fun checkIngressAuth(request: HttpServletRequest) {
        val auth: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        val accessToken: String = auth?.substring(authType.length + 1) ?: ""

        if (!hasText(accessToken)) {
            throw JwtException("Missing access token")
        }

        jwsValidator.validate(accessToken)
    }

    override fun addAuthHeaderIfNeeded(headers: TreeMap<String, String>) {
        if (egressAuthWaived()) {
            return
        }

        val token = egressTokenGetter.getServiceUserToken().accessToken
        val auth = "$authType $token"
        headers[HttpHeaders.AUTHORIZATION] = auth

        if (consumerTokenRequired()) {
            headers[consumerTokenHeaderName] = auth
        }
    }
}
