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
            "nav-cons-pen-pen-batch-oppdragWeb/sca/PENOppdragBatchWSEXP", // sca = Service Component Architecture
            "nav-cons-pen-pen-fullmaktWeb/sca/PENFullmaktWSEXP",
            "nav-cons-pen-pen-inntektWeb/sca/PENInntektWSEXP",
            "nav-cons-pen-pen-oppdragWeb/sca/PENOppdragWSEXP",
            "nav-cons-pen-pen-personWeb/sca/PENPersonWSEXP",
            "nav-cons-pen-pen-ppen003Web/sca/PENPPEN003WSEXP",
            "nav-cons-pen-pen-ppen006Web/sca/PENPPEN006WSEXP",
            "nav-cons-pen-pen-ppen008Web/sca/PPEN008WSEXP",
            "nav-cons-pen-pen-ppen015Web/sca/PENPPEN015WSEXP",
            "nav-cons-pen-pen-tjenestepensjonWeb/sca/PENTjenestepensjonWSEXP",
            "nav-cons-pen-pselv-brukerprofilWeb/sca/PSELVBrukerprofilWSEXP",
            "nav-cons-pen-pselv-fullmaktWeb/sca/PSELVFullmaktWSEXP",
            "nav-cons-pen-pselv-personWeb/sca/PSELVPersonWSEXP",
            "nav-cons-pen-pselv-ppen003Web/sca/PSELVPPEN003WSEXP",
            "nav-cons-pen-pselv-ppen004Web/sca/PENPPEN004WSEXP",
            "nav-cons-pen-pselv-samhandlerWeb/sca/PSELVSamhandlerWSEXP",
            "nav-cons-pen-pselv-tjenestepensjonWeb/sca/PSELVTjenestepensjonWSEXP",
            "nav-cons-pen-pselv-utbetalingWeb/sca/PSELVUtbetalingWSEXP",
            "nav-tjeneste-behandleTrekk_v1Web/sca/BehandleTrekkWSEXP",
            "nav-tjeneste-institusjonsopphold_v1Web/sca/InstitusjonsoppholdWSEXP",
            "nav-tjeneste-journal_v2Web/sca/JournalWSEXP",
            "nav-tjeneste-journalbehandling_v1Web/sca/JournalbehandlingWSEXP",
            "nav-tjeneste-person_v2Web/sca/PersonWSEXP",
            "nav-tjeneste-trekk_v1Web/sca/TrekkWSEXP",
            "nav-tjeneste-utbetaling_v1Web/sca/UtbetalingWSEXP",
            "pen/services/Vedtak_v2",
            "pensjon-microflow-behandleEndretForventetArbeidsinntektProxyWeb/sca/BehandleEndretForventetArbeidsinntektWSEXP",
            "pensjon-tjeneste-iverksattVedtakBehandling_v1Web/sca/IverksattVedtakBehandlingWSEXP",
            "pensjon-tjeneste-samordning_v1Web/sca/SamordningWSEXP",
            "nav-tjeneste-arkiv_v1Web/sca/ArkivWSEXP"])
    fun handlePostRequest(@RequestBody body: String, request: HttpServletRequest) = super.doPost(request, body)
}
