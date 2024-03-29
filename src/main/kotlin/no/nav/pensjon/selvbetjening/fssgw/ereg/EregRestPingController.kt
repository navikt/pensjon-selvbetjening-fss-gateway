package no.nav.pensjon.selvbetjening.fssgw.ereg

import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.common.UnprotectedControllerBase
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.servlet.http.HttpServletRequest

@RestController
@RequestMapping("ereg")
class EregRestPingController(
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    @Value("\${modapp.url}") egressEndpoint: String)
    : UnprotectedControllerBase(serviceClient, callIdGenerator, egressEndpoint) {

    @GetMapping("/api/ping")
    fun handleGetRequest(request: HttpServletRequest): ResponseEntity<String> {
        return super.doGet(request)
    }
}
