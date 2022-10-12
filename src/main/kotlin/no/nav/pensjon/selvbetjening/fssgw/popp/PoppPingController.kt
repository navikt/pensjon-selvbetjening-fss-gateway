package no.nav.pensjon.selvbetjening.fssgw.popp

import no.nav.pensjon.selvbetjening.fssgw.common.ProtectedControllerBase
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("popp")
class PoppPingController(
    authValidator: JwsValidator,
    egressTokenGetter: ServiceTokenGetter,
    serviceClient: ServiceClient,
    @Value("\${popp.url}") egressEndpoint: String) :
    ProtectedControllerBase(authValidator, egressTokenGetter, serviceClient, egressEndpoint) {

    @GetMapping(
        value = [
            "api/beholdning/ping",
            "api/opptjeningsgrunnlag/ping"])
    fun handleGetRequest(request: HttpServletRequest): ResponseEntity<String> {
        return super.doGet(request)
    }

    override fun egressAuthWaived(): Boolean {
        return false // POPP requires auth for ping
    }

    override fun consumerTokenRequired(): Boolean {
        return false
    }
}
