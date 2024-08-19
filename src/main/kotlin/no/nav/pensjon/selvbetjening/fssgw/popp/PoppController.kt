package no.nav.pensjon.selvbetjening.fssgw.popp

import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.EgressHeaderAuthController
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.servlet.http.HttpServletRequest

@RestController
@RequestMapping("popp")
class PoppController(
    jwsValidator: JwsValidator,
    egressTokenGetter: ServiceTokenGetter,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    @Value("\${popp.url}") egressEndpoint: String
) : EgressHeaderAuthController(
    jwsValidator, serviceClient, callIdGenerator, egressEndpoint, egressTokenGetter
) {
    @GetMapping(
        value = [
            "api/opptjeningsgrunnlag/{pid}",
            "api/pensjonspoeng/{pid}",
            "api/restpensjon/{pid}"])
    fun handleGetRequest(request: HttpServletRequest): ResponseEntity<String> = super.doGet(request)

    @PostMapping("api/beholdning")
    fun handlePostRequest(@RequestBody body: String, request: HttpServletRequest): ResponseEntity<String> =
        super.doPost(request, body, serviceUserId = 1)

    override fun consumerTokenRequired(): Boolean = false

    /**
     * Override in order to hide PID (which is part of request URI)
     */
    override fun metricDetail(request: HttpServletRequest): String = "POPP"
}
