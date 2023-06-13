package no.nav.pensjon.selvbetjening.fssgw.sts

import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.EgressHeaderAuthController
import no.nav.pensjon.selvbetjening.fssgw.common.EgressHeaderBasicAuthController
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import jakarta.servlet.http.HttpServletRequest

@RestController
@RequestMapping("rest/v1/sts")
class StsController(
    ingressTokenValidator: JwsValidator,
    egressTokenGetter: ServiceTokenGetter,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    @Value("\${sts.url}") egressEndpoint: String)
    : EgressHeaderAuthController(
    ingressTokenValidator, serviceClient, callIdGenerator, egressEndpoint, egressTokenGetter) {

    @GetMapping("token")
    fun handleGetRequest(request: HttpServletRequest) = super.doGet(request)

    override fun consumerTokenRequired() = false
}

@RestController
@RequestMapping("rest/v1/sts")
class StsPostController(
    ingressTokenValidator: JwsValidator,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    @Value("\${sts.url}") egressEndpoint: String,
    @Value("\${sts.username}") serviceUsername: String,
    @Value("\${sts.password}") servicePassword: String)
    : EgressHeaderBasicAuthController(
    ingressTokenValidator, serviceClient, callIdGenerator, egressEndpoint, serviceUsername, servicePassword) {

    @PostMapping("token/exchange")
    fun handlePostRequest(@RequestBody body: String, request: HttpServletRequest) = super.doPost(request, body)
}
