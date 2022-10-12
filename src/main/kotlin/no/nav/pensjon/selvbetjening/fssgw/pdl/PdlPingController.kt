package no.nav.pensjon.selvbetjening.fssgw.pdl

import no.nav.pensjon.selvbetjening.fssgw.common.ProtectedControllerBase
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("graphql")
class PdlPingController(
    authValidator: JwsValidator,
    egressTokenGetter: ServiceTokenGetter, // not used, since egress auth is waived
    serviceClient: ServiceClient,
    @Value("\${pdl.url}") egressEndpoint: String) :
    ProtectedControllerBase(authValidator, egressTokenGetter, serviceClient, egressEndpoint) {

    @RequestMapping(method = [RequestMethod.OPTIONS])
    fun handleOptionsRequest(request: HttpServletRequest): ResponseEntity<String> {
        return super.doOptions(request)
    }

    override fun egressAuthWaived(): Boolean {
        return true
    }

    override fun consumerTokenRequired(): Boolean {
        return false
    }
}
