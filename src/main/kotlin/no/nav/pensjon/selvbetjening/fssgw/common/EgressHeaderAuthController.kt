package no.nav.pensjon.selvbetjening.fssgw.common

import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.springframework.http.HttpHeaders
import java.util.*
import jakarta.servlet.http.HttpServletRequest

/**
 * Controller used for requests where credentials are to be inserted into one or more request headers.
 * Typically used for REST calls using service user token (STS).
 */
abstract class EgressHeaderAuthController(
    ingressTokenValidator: JwsValidator,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    egressEndpoint: String,
    private val egressTokenGetter: ServiceTokenGetter)
    : TokenProtectedController(ingressTokenValidator, serviceClient, callIdGenerator, egressEndpoint) {

    protected abstract fun consumerTokenRequired(): Boolean

    override fun provideHeaderAuth(request: HttpServletRequest, headers: TreeMap<String, String>) {
        val token = egressTokenGetter.getServiceUserToken().accessToken
        val auth = "$AUTH_TYPE $token"
        headers[HttpHeaders.AUTHORIZATION] = auth

        if (consumerTokenRequired()) {
            headers[CONSUMER_TOKEN_HEADER_NAME] = auth
        }
    }

    override fun provideBodyAuth(body: String) = body

    companion object {
        private const val AUTH_TYPE = "Bearer"
    }
}
