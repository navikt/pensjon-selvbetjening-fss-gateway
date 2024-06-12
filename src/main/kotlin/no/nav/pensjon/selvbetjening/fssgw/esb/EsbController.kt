package no.nav.pensjon.selvbetjening.fssgw.esb

import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.EgressBodyAuthController
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

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
            "nav-cons-pen-pen-fullmaktWeb/sca/PENFullmaktWSEXP", // sca = Service Component Architecture
            "nav-cons-pen-pen-inntektWeb/sca/PENInntektWSEXP",
            "nav-cons-pen-pen-oppdragWeb/sca/PENOppdragWSEXP",
            "nav-cons-pen-pen-personWeb/sca/PENPersonWSEXP",
            "nav-cons-pen-pselv-brukerprofilWeb/sca/PSELVBrukerprofilWSEXP",
            "nav-cons-pen-pselv-personWeb/sca/PSELVPersonWSEXP",
            "nav-cons-pen-pselv-tjenestepensjonWeb/sca/PSELVTjenestepensjonWSEXP",
            "nav-cons-pen-pselv-utbetalingWeb/sca/PSELVUtbetalingWSEXP",
            "nav-tjeneste-behandleTrekk_v1Web/sca/BehandleTrekkWSEXP",
            "nav-tjeneste-institusjonsopphold_v1Web/sca/InstitusjonsoppholdWSEXP",
            "nav-tjeneste-journal_v2Web/sca/JournalWSEXP",
            "nav-tjeneste-journalbehandling_v1Web/sca/JournalbehandlingWSEXP",
            "nav-tjeneste-person_v2Web/sca/PersonWSEXP",
            "nav-tjeneste-trekk_v1Web/sca/TrekkWSEXP",
            "nav-tjeneste-utbetaling_v1Web/sca/UtbetalingWSEXP",
            "pen/services/Vedtak_v2"])
    fun handlePostRequest(@RequestBody body: String, request: HttpServletRequest) =
        super.doPost(request, body, useServiceUser2 = false)
}
