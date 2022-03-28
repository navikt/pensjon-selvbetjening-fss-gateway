package no.nav.pensjon.selvbetjening.fssgw.popp

import no.nav.pensjon.selvbetjening.fssgw.common.BasicProtectedControllerBase
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.basicauth.BasicAuthValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("popp")
class PoppPingController(
    authValidator: BasicAuthValidator,
    serviceClient: ServiceClient,
    @Value("\${popp.url}") egressEndpoint: String) :
    BasicProtectedControllerBase(authValidator, serviceClient, egressEndpoint) {

    @GetMapping(
        value = [
            "api/beholdning/ping",
            "api/opptjeningsgrunnlag/ping"])
    fun handleGetRequest(request: HttpServletRequest): ResponseEntity<String> {
        return super.doGet(request)
    }
}