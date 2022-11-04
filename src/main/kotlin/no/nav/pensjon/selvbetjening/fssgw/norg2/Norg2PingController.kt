package no.nav.pensjon.selvbetjening.fssgw.norg2

import no.nav.pensjon.selvbetjening.fssgw.common.BasicProtectedControllerBase
import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.basicauth.BasicAuthValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("norg2")
class Norg2PingController(
    authValidator: BasicAuthValidator,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    @Value("\${norg2.url}") egressEndpoint: String)
    : BasicProtectedControllerBase(authValidator, serviceClient, callIdGenerator, egressEndpoint) {

    @GetMapping("api/v1/enhet")
    fun handleGetRequest(request: HttpServletRequest): ResponseEntity<String> {
        return super.doGet(request)
    }
}
