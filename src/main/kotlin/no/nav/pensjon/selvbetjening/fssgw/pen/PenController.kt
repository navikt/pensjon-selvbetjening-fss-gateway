package no.nav.pensjon.selvbetjening.fssgw.pen

import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.EgressHeaderAuthController
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("pen")
class PenController(
    jwsValidator: JwsValidator,
    egressTokenGetter: ServiceTokenGetter,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    @Value("\${pen.url}") egressEndpoint: String)
    : EgressHeaderAuthController(jwsValidator, serviceClient, callIdGenerator, egressEndpoint, egressTokenGetter) {

    @GetMapping(
        value = [
            "api/person/afphistorikk",
            "api/person/uforehistorikk",
            "api/uttaksgrad/person",
            "api/uttaksgrad/search",
            "springapi/krav",
            "springapi/krav/{kravId}",
            "springapi/sak/sammendrag",
            "springapi/vedtak",
            "springapi/vedtak/bestemgjeldende",
            "springapi/vedtak/{vedtakId}/beregninger"])
    fun handleGetRequest(request: HttpServletRequest): ResponseEntity<String> {
        return super.doGet(request)
    }

    @PostMapping("api/soknad/alderspensjon/behandle")
    fun handlePostRequest(@RequestBody body: String, request: HttpServletRequest): ResponseEntity<String> {
        return super.doPost(request, body)
    }

    override fun consumerTokenRequired(): Boolean {
        return false
    }
}
