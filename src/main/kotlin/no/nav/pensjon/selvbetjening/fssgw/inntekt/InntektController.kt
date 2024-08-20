package no.nav.pensjon.selvbetjening.fssgw.inntekt

import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.EgressHeaderAuthController
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.servlet.http.HttpServletRequest

const val PATH = "api/v1/"

@RestController
@RequestMapping("inntektskomponenten-ws/rs")
class InntektController(
    ingressTokenValidator: JwsValidator,
    egressTokenGetter: ServiceTokenGetter,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    @Value("\${inntekt.url}") egressEndpoint: String
) : EgressHeaderAuthController(
    ingressTokenValidator, serviceClient, callIdGenerator, egressEndpoint, egressTokenGetter
) {
    @GetMapping("${PATH}forventetinntekt")
    fun handleGetRequest(request: HttpServletRequest): ResponseEntity<String> {
        return super.doGet(request)
    }

    @PostMapping(
        value = [
            "${PATH}forventetinntekt",
            "${PATH}hentdetaljerteabonnerteinntekter",
            "${PATH}hentabonnerteinntekterbolk"])
    fun handlePostRequest(@RequestBody body: String, request: HttpServletRequest): ResponseEntity<String> =
        super.doPost(request, body, serviceUserId = 1)

    override fun consumerTokenRequired(): Boolean = true
}
