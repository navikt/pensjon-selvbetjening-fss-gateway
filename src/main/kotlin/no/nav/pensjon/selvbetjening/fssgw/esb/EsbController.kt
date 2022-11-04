package no.nav.pensjon.selvbetjening.fssgw.esb

import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.EgressBodyAuthController
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
class EsbController(
    ingressTokenValidator: JwsValidator,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    @Value("\${esb.url}") egressEndpoint: String,
    @Value("\${sts.password}") private val password: String)
    : EgressBodyAuthController(ingressTokenValidator, serviceClient, callIdGenerator, egressEndpoint, password) {

    @PostMapping(
        value = [
            "nav-cons-pen-pen-personWeb/sca/PENPersonWSEXP", // sca = Service Component Architecture
            "nav-cons-pen-pen-ppen015Web/sca/PENPPEN015WSEXP",
            "nav-cons-pen-pselv-brukerprofilWeb/sca/PSELVBrukerprofilWSEXP",
            "nav-cons-pen-pselv-fullmaktWeb/sca/PSELVFullmaktWSEXP",
            "nav-cons-pen-pselv-henvendelseWeb/sca/PSELVHenvendelseWSEXP",
            "nav-cons-pen-pselv-inntektWeb/sca/PSELVInntektWSEXP",
            "nav-cons-pen-pselv-personWeb/sca/PSELVPersonWSEXP",
            "nav-cons-pen-pselv-ppen004Web/sca/PENPPEN004WSEXP",
            "nav-cons-pen-pselv-samhandlerWeb/sca/PSELVSamhandlerWSEXP",
            "nav-cons-pen-pselv-tjenestepensjonWeb/sca/PSELVTjenestepensjonWSEXPP",
            "nav-cons-pen-pselv-utbetalingWeb/sca/PSELVUtbetalingWSEXP",
            "nav-tjeneste-journal_v2Web/sca/JournalWSEXP"])
    fun handlePostRequest(@RequestBody body: String, request: HttpServletRequest): ResponseEntity<String> {
        return super.doPost(request, body)
    }
}
