package no.nav.pensjon.selvbetjening.fssgw.pen

import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.EgressHeaderAuthController
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("pen")
class PersonPingController(
        ingressTokenValidator: JwsValidator,
        egressTokenGetter: ServiceTokenGetter,
        serviceClient: ServiceClient,
        callIdGenerator: CallIdGenerator,
        @Value("\${pen.url}") egressEndpoint: String)
    : EgressHeaderAuthController(
        ingressTokenValidator, serviceClient, callIdGenerator, egressEndpoint, egressTokenGetter) {

    @GetMapping("api/person/ping")
    fun handleGetRequest(request: HttpServletRequest)= super.doGet(request)

    override fun consumerTokenRequired() = false
}
