package no.nav.pensjon.selvbetjening.fssgw.common

import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import org.springframework.http.HttpHeaders
import java.util.*
import jakarta.servlet.http.HttpServletRequest

/**
 * Controller used for requests where basic auth credentials are to be inserted into the Authorization header.
 */
abstract class EgressHeaderBasicAuthController(
    ingressTokenValidator: JwsValidator,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    egressEndpoint: String,
    serviceUsername: String,
    servicePassword: String)
    : TokenProtectedController(ingressTokenValidator, serviceClient, callIdGenerator, egressEndpoint) {

    private val credentials = Base64.getEncoder().encodeToString("$serviceUsername:$servicePassword".toByteArray())

    override fun provideHeaderAuth(request: HttpServletRequest, headers: TreeMap<String, String>) {
        headers[HttpHeaders.AUTHORIZATION] = "$AUTH_TYPE $credentials"
    }

    override fun provideBodyAuth(body: String) = body

    companion object {
        private const val AUTH_TYPE = "Basic"
    }
}
