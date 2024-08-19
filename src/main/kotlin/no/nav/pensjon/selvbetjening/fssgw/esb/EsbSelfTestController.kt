package no.nav.pensjon.selvbetjening.fssgw.esb

import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.EgressBodyAuthController
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import jakarta.servlet.http.HttpServletRequest

@RestController
class EsbSelfTestController(
    ingressTokenValidator: JwsValidator,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    @Value("\${esb.url}") egressEndpoint: String,
    @Value("\${sts.password}") private val password: String)
    : EgressBodyAuthController(
    ingressTokenValidator, serviceClient, callIdGenerator, egressEndpoint, password
) {
    @PostMapping("nav-cons-test-getapplicationversionWeb/sca/TESTGetApplicationVersionWSEXP")
    fun handlePostRequest(@RequestBody body: String, request: HttpServletRequest): ResponseEntity<String> =
        super.doPost(request, body, serviceUserId = 1)
}
