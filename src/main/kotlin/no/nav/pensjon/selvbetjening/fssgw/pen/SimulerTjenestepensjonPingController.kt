package no.nav.pensjon.selvbetjening.fssgw.pen

import no.nav.pensjon.selvbetjening.fssgw.common.BasicProtectedControllerBase
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.basicauth.BasicAuthValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("pen")
class SimulerTjenestepensjonPingController(
    authValidator: BasicAuthValidator,
    private val egressTokenGetter: ServiceTokenGetter,
    serviceClient: ServiceClient,
    @Value("\${pen.url}") egressEndpoint: String) :
    BasicProtectedControllerBase(authValidator, serviceClient, egressEndpoint) {

    @GetMapping("api/simuler/tjenestepensjon/ping")
    fun handleGetRequest(request: HttpServletRequest): ResponseEntity<String> {
        return super.doGet(request)
    }

    override fun addAuthHeaderIfNeeded(request: HttpServletRequest, headers: TreeMap<String, String>) {
        val token = egressTokenGetter.getServiceUserToken().accessToken
        headers[HttpHeaders.AUTHORIZATION] = "Bearer $token"
    }
}
