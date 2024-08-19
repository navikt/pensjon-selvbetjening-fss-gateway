package no.nav.pensjon.selvbetjening.fssgw.org.ws

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
@RequestMapping("ws/OrganisasjonService")
class OrganisasjonController(
    ingressTokenValidator: JwsValidator,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    @Value("\${ereg.ws.url}") egressEndpoint: String,
    @Value("\${sts.password}") private val password: String
) : EgressBodyAuthController(
    ingressTokenValidator, serviceClient, callIdGenerator, egressEndpoint, password
) {
    @PostMapping("v4")
    fun handlePostRequest(@RequestBody body: String, request: HttpServletRequest): ResponseEntity<String> =
        super.doPost(request, body, serviceUserId = 1)
}
