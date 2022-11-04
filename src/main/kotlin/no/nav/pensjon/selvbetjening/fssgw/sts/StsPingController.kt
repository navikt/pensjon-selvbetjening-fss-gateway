package no.nav.pensjon.selvbetjening.fssgw.sts

import no.nav.pensjon.selvbetjening.fssgw.common.BasicProtectedControllerBase
import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.basicauth.BasicAuthValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("rest/v1/sts")
class StsPingController(
    authValidator: BasicAuthValidator,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    @Value("\${sts.url}") egressEndpoint: String)
    : BasicProtectedControllerBase(authValidator, serviceClient, callIdGenerator, egressEndpoint) {

    @GetMapping(value = ["samltoken", "token"])
    fun handleGetRequest(request: HttpServletRequest): ResponseEntity<String> {
        return super.doGet(request)
    }

    override fun provideHeaderAuth(request: HttpServletRequest, headers: TreeMap<String, String>) {
        headers[HttpHeaders.AUTHORIZATION] = request.getHeader(HttpHeaders.AUTHORIZATION)
    }
}
