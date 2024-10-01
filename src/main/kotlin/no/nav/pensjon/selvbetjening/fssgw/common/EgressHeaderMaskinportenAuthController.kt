package no.nav.pensjon.selvbetjening.fssgw.common

import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.maskinporten.MaskinportenTokenGetter
import org.springframework.http.HttpHeaders
import java.util.*

/**
 * Controller used for requests where credentials are to be inserted into one or more request headers.
 * For REST calls using maskinporten token.
 */
abstract class EgressHeaderMaskinportenAuthController(
    ingressTokenValidator: JwsValidator,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    egressEndpoint: String,
    private val egressTokenGetter: MaskinportenTokenGetter)
    : TokenProtectedController(ingressTokenValidator, serviceClient, callIdGenerator, egressEndpoint) {

    override fun provideHeaderAuth(
        request: HttpServletRequest,
        headers: TreeMap<String, String>,
        serviceUserId: Int
    ) {
        val token = headers["scope"]?.let { egressTokenGetter.getToken(it) }?.access_token
        val auth = "$AUTH_TYPE $token"
        headers[HttpHeaders.AUTHORIZATION] = auth
        headers.remove("scope")
    }

    override fun provideBodyAuth(body: String) = body

    companion object {
        private const val AUTH_TYPE = "Bearer"
    }
}
