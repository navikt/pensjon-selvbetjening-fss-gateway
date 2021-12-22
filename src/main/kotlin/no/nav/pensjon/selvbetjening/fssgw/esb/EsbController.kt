package no.nav.pensjon.selvbetjening.fssgw.esb

import no.nav.pensjon.selvbetjening.fssgw.common.ControllerBase
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping(
    value = [
        "nav-cons-pen-pselv-fullmaktWeb",
        "nav-cons-pen-pselv-personWeb",
        "nav-cons-test-getapplicationversionWeb"])
class EsbController(
    jwsValidator: JwsValidator,
    egressTokenGetter: ServiceTokenGetter,
    serviceClient: ServiceClient,
    @Value("\${esb.url}") egressEndpoint: String) :
    ControllerBase(jwsValidator, serviceClient, egressTokenGetter, egressEndpoint) {

    @PostMapping(
        value = [
            "sca/PSELVFullmaktWSEXP", // sca = Service Component Architecture
            "sca/PSELVPersonWSEXP",
            "sca/TESTGetApplicationVersionWSEXP"])
    fun handlePostRequest(@RequestBody body: String, request: HttpServletRequest): ResponseEntity<String> {
        return super.doPost(request, body)
    }

    override fun egressAuthWaived(): Boolean {
        return false
    }
}
