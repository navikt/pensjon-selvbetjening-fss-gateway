package no.nav.pensjon.selvbetjening.fssgw.partner

import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.EgressHeaderMaskinportenAuthController
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.maskinporten.MaskinportenTokenGetter
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class SimulerSPKTjenestepensjonController(
    ingressTokenValidator: JwsValidator,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    egressTokenGetter: MaskinportenTokenGetter,
    @Value("\${spk.url}") egressEndpoint: String
) : EgressHeaderMaskinportenAuthController(
    ingressTokenValidator, serviceClient, callIdGenerator, egressEndpoint, egressTokenGetter
) {

    @PostMapping("medlem/pensjon/prognose/v1")
    fun handlePostRequest(
        @RequestBody body: String,
        @RequestHeader("scope", required = true) scope: String,
        request: HttpServletRequest
    ): ResponseEntity<String> {
        return doPost(request = request, body = body, externalCall = true)
    }
}