package no.nav.pensjon.selvbetjening.fssgw.aareg

import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.EgressHeaderAuthController
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("aareg-services")
class AaregController(
    jwsValidator: JwsValidator,
    egressTokenGetter: ServiceTokenGetter,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    @Value("\${aareg.url}") egressEndpoint: String)
    : EgressHeaderAuthController(jwsValidator, serviceClient, callIdGenerator, egressEndpoint, egressTokenGetter) {

    @GetMapping("api/v1/arbeidstaker/arbeidsforhold")
    fun handleGetRequest(request: HttpServletRequest): ResponseEntity<String> {
        return super.doGet(request)
    }

    override fun consumerTokenRequired(): Boolean {
        return true
    }
}
