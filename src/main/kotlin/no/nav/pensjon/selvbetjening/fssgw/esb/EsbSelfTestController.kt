package no.nav.pensjon.selvbetjening.fssgw.esb

import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.common.UnprotectedControllerBase
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
class EsbSelfTestController(
    serviceClient: ServiceClient,
    @Value("\${esb.url}") egressEndpoint: String) :
    UnprotectedControllerBase(serviceClient, egressEndpoint) {

    @PostMapping("nav-cons-test-getapplicationversionWeb/sca/TESTGetApplicationVersionWSEXP")
    fun handlePostRequest(@RequestBody body: String, request: HttpServletRequest): ResponseEntity<String> {
        return super.doPost(request, body)
    }
}
