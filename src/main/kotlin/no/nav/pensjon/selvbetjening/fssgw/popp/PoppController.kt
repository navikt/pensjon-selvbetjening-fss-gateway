package no.nav.pensjon.selvbetjening.fssgw.popp

import no.nav.pensjon.selvbetjening.fssgw.common.ControllerBase
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("popp")
class PoppController(
    jwsValidator: JwsValidator,
    egressTokenGetter: ServiceTokenGetter,
    serviceClient: ServiceClient,
    @Value("\${popp.url}") egressEndpoint: String) :
    ControllerBase(jwsValidator, serviceClient, egressTokenGetter, egressEndpoint) {

    @GetMapping(
        value = [
            "api/opptjeningsgrunnlag/{pid}",
            "api/pensjonspoeng/{pid}",
            "api/restpensjon/{pid}"])
    fun handleGetRequest(request: HttpServletRequest): ResponseEntity<String> {
        return super.doGet(request)
    }

    @PostMapping("api/beholdning")
    fun handlePostRequest(request: HttpServletRequest): ResponseEntity<String> {
        return super.doGet(request)
    }

    override fun egressAuthWaived(): Boolean {
        return false
    }

    override fun consumerTokenRequired(): Boolean {
        return false
    }
}
