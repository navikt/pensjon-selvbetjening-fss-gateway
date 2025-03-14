package no.nav.pensjon.selvbetjening.fssgw.regler

import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.EgressNoAuthController
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.servlet.http.HttpServletRequest

@RestController
class PensjonReglerController(
    ingressTokenValidator: JwsValidator,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    @Value("\${pensjonregler.url}") egressEndpoint: String
) : EgressNoAuthController(
    ingressTokenValidator, serviceClient, callIdGenerator, egressEndpoint
) {
    @GetMapping(
        value = [
            "api/merknad",
            "info"])
    fun handleGetRequest(request: HttpServletRequest): ResponseEntity<String> = super.doGet(request)

    @PostMapping(
        value = [
            "api/beregnAfpPrivat",
            "api/beregnAlderspensjon2011ForsteUttak",
            "api/beregnAlderspensjon2016ForsteUttak",
            "api/beregnAlderspensjon2025ForsteUttak",
            "api/beregnOpptjening",
            "api/beregnPensjonsBeholdning",
            "api/beregnPoengtallBatch",
            "api/delingstall",
            "api/fastsettTrygdetid",
            "api/hentGrunnbelopListe",
            "api/hentGyldigSats",
            "api/regulerPensjonsbeholdning",
            "api/revurderingAlderspensjon2011",
            "api/revurderingAlderspensjon2016",
            "api/revurderingAlderspensjon2025",
            "api/simulerAFP",
            "api/simulerAlderspensjon",
            "api/simulerVilkarsprovAFP",
            "api/vilkarsprovAlderspensjon2011",
            "api/vilkarsprovAlderspensjon2016",
            "api/vilkarsprovAlderspensjon2025",
            "api/vilkarsprovAlderspensjonOver67"])
    fun handlePostRequest(@RequestBody body: String, request: HttpServletRequest): ResponseEntity<String> =
        doPost(request, body)
}
