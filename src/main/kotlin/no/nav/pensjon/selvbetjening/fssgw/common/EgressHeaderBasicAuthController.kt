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
    private val serviceUserCredentials: Map<Int, String>
) : TokenProtectedController(ingressTokenValidator, serviceClient, callIdGenerator, egressEndpoint) {

    override fun provideHeaderAuth(
        request: HttpServletRequest,
        headers: TreeMap<String, String>,
        serviceUserId: Int
    ) {
        headers[HttpHeaders.AUTHORIZATION] = "$AUTH_TYPE ${encodedCredentials(serviceUserId)}"
    }

    override fun provideBodyAuth(body: String) = body

    private fun encodedCredentials(serviceUserId: Int): String =
        serviceUserCredentials[serviceUserId]?.let(::base64Encode)
            ?: throw IllegalArgumentException("Invalid serviceUserId: $serviceUserId")


    companion object {
        private const val AUTH_TYPE = "Basic"

        private fun base64Encode(value: String): String =
            Base64.getEncoder().encodeToString(value.toByteArray())
    }
}
