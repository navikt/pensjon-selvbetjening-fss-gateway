package no.nav.pensjon.selvbetjening.fssgw.vedtak

import no.nav.pensjon.selvbetjening.fssgw.common.BasicProtectedControllerBase
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.basicauth.BasicAuthValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("pen/services/Vedtak_v2")
class VedtakPingController(
    authValidator: BasicAuthValidator,
    serviceClient: ServiceClient,
    @Value("\${pen.url}") egressEndpoint: String) :
    BasicProtectedControllerBase(authValidator, serviceClient, egressEndpoint) {

    @GetMapping("ping")
    fun handleGetRequest(request: HttpServletRequest): ResponseEntity<String> {
        return super.doGet(request)
    }
}
