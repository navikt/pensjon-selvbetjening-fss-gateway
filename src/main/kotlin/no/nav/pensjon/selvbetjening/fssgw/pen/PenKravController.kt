package no.nav.pensjon.selvbetjening.fssgw.pen

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
@RequestMapping("pen")
class PenKravController(
    ingressTokenValidator: JwsValidator,
    egressTokenGetter: ServiceTokenGetter,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    @Value("\${pen.url}") egressEndpoint: String
) : EgressHeaderAuthController(
    ingressTokenValidator, serviceClient, callIdGenerator, egressEndpoint, egressTokenGetter
) {
    @GetMapping("springapi/krav/{kravId}")
    fun handleGetRequest(request: HttpServletRequest): ResponseEntity<String> = super.doGet(request)

    override fun consumerTokenRequired() = false

    /**
     * Override in order to exclude krav-ID from metric
     */
    override fun metricDetail(request: HttpServletRequest) = "pen/springapi/krav/id"
}
