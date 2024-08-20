package no.nav.pensjon.selvbetjening.fssgw.tss

import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.EgressHeaderAuthController
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * TSS = Tjenestebasert samhandlersystem
 */
@RestController
@RequestMapping("services/tss")
class TssController(
    jwsValidator: JwsValidator,
    egressTokenGetter: ServiceTokenGetter,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    @Value("\${wasapp.url}") egressEndpoint: String
) : EgressHeaderAuthController(
    jwsValidator, serviceClient, callIdGenerator, egressEndpoint, egressTokenGetter
) {
    @PostMapping("hentSamhandler")
    fun handlePostRequest(@RequestBody body: String, request: HttpServletRequest) =
        super.doPost(request, body, serviceUserId = 1)

    override fun consumerTokenRequired() = false
}
