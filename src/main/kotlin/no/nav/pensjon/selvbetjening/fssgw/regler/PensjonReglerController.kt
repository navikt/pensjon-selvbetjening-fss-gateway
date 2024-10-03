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
    jwsValidator: JwsValidator,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    @Value("\${pensjonregler.url}") egressEndpoint: String
) : EgressNoAuthController(
    jwsValidator, serviceClient, callIdGenerator, egressEndpoint
) {
    @GetMapping(
        value = [
            "api/merknad",
            "info"])
    fun handleGetRequest(request: HttpServletRequest): ResponseEntity<String> = super.doGet(request)

    @PostMapping(
        value = [
            "api/beregnAlderspensjon2025ForsteUttak",
            "api/beregnOpptjening",
            "api/beregnPensjonsBeholdning",
            "api/beregnPoengtallBatch",
            "api/fastsettTrygdetid",
            "api/hentGrunnbelopListe",
            "api/revurderingAlderspensjon2025",
            "api/simulerAlderspensjon",
            "api/vilkarsprovAlderspensjonOver67"])
    fun handlePostRequest(@RequestBody body: String, request: HttpServletRequest): ResponseEntity<String> =
        doPost(request, body)
}
