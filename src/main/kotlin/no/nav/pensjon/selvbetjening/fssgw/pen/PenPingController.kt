package no.nav.pensjon.selvbetjening.fssgw.pen

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
@RequestMapping("pen")
class PenPingController(
    authValidator: BasicAuthValidator,
    serviceClient: ServiceClient,
    @Value("\${pen.url}") egressEndpoint: String) :
    BasicProtectedControllerBase(authValidator, serviceClient, egressEndpoint) {

    @GetMapping(
        value = [
            "services/Vedtak_v2",
            "api/simuler/tjenestepensjon/ping",
            "springapi/ping"])
    fun handleGetRequest(request: HttpServletRequest): ResponseEntity<String> {
        return super.doGet(request)
    }
}
