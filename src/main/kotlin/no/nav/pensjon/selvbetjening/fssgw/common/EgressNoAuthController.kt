package no.nav.pensjon.selvbetjening.fssgw.common

import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import java.util.*
import jakarta.servlet.http.HttpServletRequest

/**
 * Controller for requests that require no egress auth.
 */
abstract class EgressNoAuthController(
    tokenValidator: JwsValidator,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    egressEndpoint: String)
    : TokenProtectedController(tokenValidator, serviceClient, callIdGenerator, egressEndpoint) {

    override fun provideHeaderAuth(
        request: HttpServletRequest,
        headers: TreeMap<String, String>,
        serviceUserId: Int
    ) {
        // No operation
    }

    override fun provideBodyAuth(body: String) = body
}
