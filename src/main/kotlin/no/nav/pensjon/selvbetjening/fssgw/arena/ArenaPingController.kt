package no.nav.pensjon.selvbetjening.fssgw.arena

import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjon.selvbetjening.fssgw.common.BasicProtectedControllerBase
import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.basicauth.BasicAuthValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("ail_ws") // ail = Arena integration layers, ws = web services
class ArenaPingController(
    authValidator: BasicAuthValidator,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    @Value("\${arena.url}") egressEndpoint: String)
    : BasicProtectedControllerBase(authValidator, serviceClient, callIdGenerator, egressEndpoint) {

    @PostMapping("Arbeidsevnevurdering_v1")
    fun handlePostRequest(@RequestBody body: String, request: HttpServletRequest) = super.doPost(request, body)
}
