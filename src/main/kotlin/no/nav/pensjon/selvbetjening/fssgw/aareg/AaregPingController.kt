package no.nav.pensjon.selvbetjening.fssgw.aareg

import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.common.UnprotectedControllerBase
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("aareg-services")
class AaregPingController(
        serviceClient: ServiceClient,
        @Value("\${aareg.url}") egressEndpoint: String) :
        UnprotectedControllerBase(serviceClient, egressEndpoint) {

    @GetMapping("/api/ping")
    fun handleGetRequest(request: HttpServletRequest): ResponseEntity<String> {
        return super.doGet(request)
    }
}