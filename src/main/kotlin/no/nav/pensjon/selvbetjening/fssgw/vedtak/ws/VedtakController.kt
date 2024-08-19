package no.nav.pensjon.selvbetjening.fssgw.vedtak.ws

import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.EgressBodyAuthController
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("pen/services")
class VedtakController(
    ingressTokenValidator: JwsValidator,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    @Value("\${pen.url}") egressEndpoint: String,
    @Value("\${sts.password}") private val password: String
) : EgressBodyAuthController(
    ingressTokenValidator, serviceClient, callIdGenerator, egressEndpoint, password
) {

    @PostMapping("Vedtak_v2")
    fun handlePostRequest(@RequestBody body: String, request: HttpServletRequest) =
        super.doPost(request, body, serviceUserId = 1)
}
