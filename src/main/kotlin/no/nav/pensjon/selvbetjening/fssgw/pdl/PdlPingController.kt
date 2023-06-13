package no.nav.pensjon.selvbetjening.fssgw.pdl

import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.EgressNoAuthController
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import jakarta.servlet.http.HttpServletRequest

@RestController
@RequestMapping("graphql")
class PdlPingController(
    tokenValidator: JwsValidator,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    @Value("\${pdl.url}") egressEndpoint: String)
    : EgressNoAuthController(tokenValidator, serviceClient, callIdGenerator, egressEndpoint) {

    @RequestMapping(method = [RequestMethod.OPTIONS])
    fun handleOptionsRequest(request: HttpServletRequest): ResponseEntity<String> {
        return super.doOptions(request)
    }
}
