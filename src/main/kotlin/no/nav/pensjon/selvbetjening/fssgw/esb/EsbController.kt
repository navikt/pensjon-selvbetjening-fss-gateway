package no.nav.pensjon.selvbetjening.fssgw.esb

import no.nav.pensjon.selvbetjening.fssgw.common.ControllerBase
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
class EsbController(
    jwsValidator: JwsValidator,
    egressTokenGetter: ServiceTokenGetter,
    serviceClient: ServiceClient,
    @Value("\${esb.url}") egressEndpoint: String) :
    ControllerBase(jwsValidator, serviceClient, egressTokenGetter, egressEndpoint) {

    @PostMapping(
        value = [
            "nav-cons-pen-pselv-brukerprofilWeb/sca/PSELVBrukerprofilWSEXP", // sca = Service Component Architecture
            "nav-cons-pen-pselv-fullmaktWeb/sca/PSELVFullmaktWSEXP",
            "nav-cons-pen-pselv-henvendelseWeb/sca/PSELVHenvendelseWSEXP",
            "nav-cons-pen-pselv-inntektWeb/sca/PSELVInntektWSEXP",
            "nav-cons-pen-pselv-personWeb/sca/PSELVPersonWSEXP",
            "nav-cons-pen-pselv-ppen004Web/sca/PENPPEN004WSEXP",
            "nav-cons-pen-pselv-samhandlerWeb/sca/PSELVSamhandlerWSEXP",
            "nav-cons-pen-pselv-tjenestepensjonWeb/sca/PSELVTjenestepensjonWSEXPP",
            "nav-cons-pen-pselv-utbetalingWeb/sca/PSELVUtbetalingWSEXP",
            "nav-cons-test-getapplicationversionWeb/sca/TESTGetApplicationVersionWSEXP"])
    fun handlePostRequest(@RequestBody body: String, request: HttpServletRequest): ResponseEntity<String> {
        return super.doPost(request, body)
    }

    override fun egressAuthWaived(): Boolean {
        return false
    }

    override fun consumerTokenRequired(): Boolean {
        return false
    }
}
