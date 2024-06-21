package no.nav.pensjon.selvbetjening.fssgw.sts

import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.EgressHeaderBasicAuthController
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("rest/v1/sts")
class StsController(
    ingressTokenValidator: JwsValidator,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    @Value("\${sts.url}") egressEndpoint: String,
    @Value("\${sts.username}") serviceUsername: String,
    @Value("\${sts.password}") servicePassword: String
) : EgressHeaderBasicAuthController(
    ingressTokenValidator, serviceClient, callIdGenerator, egressEndpoint, serviceUsername, servicePassword
) {
    @GetMapping("token")
    fun handleGetRequest(request: HttpServletRequest) = super.doGet(request)

    @PostMapping("token/exchange")
    fun handlePostRequest(@RequestBody body: String, request: HttpServletRequest) =
        super.doPost(request, body, useServiceUser2 = false)
}
