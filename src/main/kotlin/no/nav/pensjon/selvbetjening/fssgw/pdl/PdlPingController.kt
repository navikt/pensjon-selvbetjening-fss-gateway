package no.nav.pensjon.selvbetjening.fssgw.pdl

import no.nav.pensjon.selvbetjening.fssgw.common.BasicProtectedControllerBase
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.basicauth.BasicAuthValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("graphql")
class PdlPingController(
    authValidator: BasicAuthValidator,
    serviceClient: ServiceClient,
    @Value("\${pdl.url}") egressEndpoint: String) :
    BasicProtectedControllerBase(authValidator, serviceClient, egressEndpoint) {

    @RequestMapping(method = [RequestMethod.OPTIONS])
    fun handleOptionsRequest(request: HttpServletRequest): ResponseEntity<String> {
        return super.doOptions(request)
    }
}
