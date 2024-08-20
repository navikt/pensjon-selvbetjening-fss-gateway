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
class PenController(
    ingressTokenValidator: JwsValidator,
    egressTokenGetter: ServiceTokenGetter,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    @Value("\${pen.url}") egressEndpoint: String
) : EgressHeaderAuthController(
    ingressTokenValidator, serviceClient, callIdGenerator, egressEndpoint, egressTokenGetter
) {
    @GetMapping(
        value = [
            "api/person/afphistorikk",
            "api/person/uforehistorikk",
            "api/uttaksgrad/person",
            "api/uttaksgrad/search",
            "springapi/krav",
            "springapi/sak/sammendrag",
            "springapi/vedtak",
            "springapi/vedtak/bestemgjeldende"])
    fun handleGetRequest(request: HttpServletRequest): ResponseEntity<String> = super.doGet(request)

    @PostMapping(
        value = [
            "api/soknad/alderspensjon/behandle",
            "springapi/simulering/alderspensjon",
            "springapi/uttaksalder"
        ])
    fun handlePostRequest(@RequestBody body: String, request: HttpServletRequest) =
        super.doPost(request, body, serviceUserId = 1)

    override fun consumerTokenRequired() = false
}
