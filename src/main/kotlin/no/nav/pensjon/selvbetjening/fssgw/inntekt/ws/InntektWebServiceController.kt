package no.nav.pensjon.selvbetjening.fssgw.inntekt.ws

import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.EgressBodyAuthController
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.servlet.http.HttpServletRequest

@RestController
@RequestMapping("inntektskomponenten-ws/inntekt")
class InntektWebServiceController(
    ingressTokenValidator: JwsValidator,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    @Value("\${app.url}") egressEndpoint: String,
    @Value("\${sts.password}") private val password: String
) : EgressBodyAuthController(
    ingressTokenValidator, serviceClient, callIdGenerator, egressEndpoint, password
) {
    @PostMapping(
        value = [
            "v3/Inntekt",
            "BehandleInntekt"])
    fun handlePostRequest(@RequestBody body: String, request: HttpServletRequest): ResponseEntity<String> =
        super.doPost(request, body, useServiceUser2 = false)
}
