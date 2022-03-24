package no.nav.pensjon.selvbetjening.fssgw.common

import no.nav.pensjon.selvbetjening.fssgw.tech.basicauth.BasicAuthValidator
import org.springframework.http.HttpHeaders
import java.util.*
import javax.security.auth.message.AuthException
import javax.servlet.http.HttpServletRequest

abstract class BasicProtectedControllerBase(
    private val authValidator: BasicAuthValidator,
    serviceClient: ServiceClient,
    egressEndpoint: String) : ControllerBase(serviceClient, egressEndpoint) {

    private val authType = "Basic"

    override fun checkIngressAuth(request: HttpServletRequest) {
        val auth: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        val credentials: String = auth?.substring(authType.length + 1) ?: ""
        val authorized = authValidator.validate(credentials)

        if (!authorized) {
            throw AuthException("Wrong or missing basic auth credentials")
        }
    }

    override fun addAuthHeaderIfNeeded(headers: TreeMap<String, String>) {
        // No operation
    }
}
