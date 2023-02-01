package no.nav.pensjon.selvbetjening.fssgw.common

import no.nav.pensjon.selvbetjening.fssgw.common.SoapPasswordInserter.insertPassword
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import java.util.*
import javax.servlet.http.HttpServletRequest

/**
 * Controller used for requests where credentials are to be inserted into the request body.
 * Typically used for SOAP requests (where the request body consists of a SOAP envelope containing a
 * SOAP security header with credentials).
 * Credentials are inserted by replacing the placeholder "__password__" with the actual password.
 */
abstract class EgressBodyAuthController(
    ingressTokenValidator: JwsValidator,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    egressEndpoint: String,
    private val password: String)
    : TokenProtectedController(ingressTokenValidator, serviceClient, callIdGenerator, egressEndpoint) {

    override fun provideBodyAuth(body: String) = insertPassword(body, password)

    override fun provideHeaderAuth(request: HttpServletRequest, headers: TreeMap<String, String>) {
        // No auth header – auth is instead provided in SOAP header (in HTTP body)
    }
}
