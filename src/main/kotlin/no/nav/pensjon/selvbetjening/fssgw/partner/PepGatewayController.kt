package no.nav.pensjon.selvbetjening.fssgw.partner

import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.EgressNoAuthController
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import jakarta.servlet.http.HttpServletRequest

/**
 * PEP = Policy enforcement point
 * Requires no additional auth to be inserted (hence EgressNoAuthController)
 */
@RestController
class PepGatewayController(
    ingressTokenValidator: JwsValidator,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    @Value("\${pep-gw.url}") egressEndpoint: String)
    : EgressNoAuthController(ingressTokenValidator, serviceClient, callIdGenerator, egressEndpoint) {

    @PostMapping("privat.pensjonsrettighetstjeneste/privatPensjonTjenesteV2_0")
    fun handlePostRequest(@RequestBody body: String, request: HttpServletRequest): ResponseEntity<String> {
        return super.doPost(request, body)
    }
}
