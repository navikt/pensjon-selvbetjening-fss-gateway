package no.nav.pensjon.selvbetjening.fssgw.pen

import no.nav.pensjon.selvbetjening.fssgw.common.ProtectedControllerBase
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("pen")
class PenPingController(
    authValidator: JwsValidator,
    egressTokenGetter: ServiceTokenGetter,
    serviceClient: ServiceClient,
    @Value("\${pen.url}") egressEndpoint: String) :
    ProtectedControllerBase(authValidator, egressTokenGetter, serviceClient, egressEndpoint) {

    @GetMapping("springapi/ping")
    fun handleGetRequest(request: HttpServletRequest): ResponseEntity<String> {
        return super.doGet(request)
    }

    override fun egressAuthWaived(): Boolean {
        return false // PEN requires auth for ping
    }

    override fun consumerTokenRequired(): Boolean {
        return false
    }
}
