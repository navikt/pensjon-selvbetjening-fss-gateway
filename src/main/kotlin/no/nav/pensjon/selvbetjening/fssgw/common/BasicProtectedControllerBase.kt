package no.nav.pensjon.selvbetjening.fssgw.common

import jakarta.security.auth.message.AuthException
import no.nav.pensjon.selvbetjening.fssgw.tech.basicauth.BasicAuthValidator
import org.springframework.http.HttpHeaders
import java.util.*
import jakarta.servlet.http.HttpServletRequest

abstract class BasicProtectedControllerBase(
    private val authValidator: BasicAuthValidator,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    egressEndpoint: String) : ControllerBase(serviceClient, callIdGenerator, egressEndpoint) {

    override fun checkIngressAuth(request: HttpServletRequest) {
        val auth = request.getHeader(HttpHeaders.AUTHORIZATION)
        val credentials = auth?.substring(AUTH_TYPE.length + 1) ?: ""
        val authorized = authValidator.validate(credentials)

        if (!authorized) {
            throw AuthException("Wrong or missing basic auth credentials")
        }
    }

    override fun provideBodyAuth(body: String) = body

    override fun provideHeaderAuth(request: HttpServletRequest, headers: TreeMap<String, String>) {
        // No auth header by default
    }

    private companion object{
        private const val AUTH_TYPE = "Basic"
    }
}
