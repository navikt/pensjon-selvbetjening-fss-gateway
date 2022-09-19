package no.nav.pensjon.selvbetjening.fssgw.tss

import no.nav.pensjon.selvbetjening.fssgw.common.ProtectedControllerBase
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

/**
 * TSS = Tjenestebasert samhandlersystem
 */
@RestController
@RequestMapping("services/tss")
class TssController(
    jwsValidator: JwsValidator,
    egressTokenGetter: ServiceTokenGetter,
    serviceClient: ServiceClient,
    @Value("\${wasapp.url}") egressEndpoint: String) :
    ProtectedControllerBase(jwsValidator, egressTokenGetter, serviceClient, egressEndpoint) {

    @PostMapping("hentSamhandler")
    fun handlePostRequest(@RequestBody body: String, request: HttpServletRequest): ResponseEntity<String> {
        return super.doPost(request, body)
    }

    override fun egressAuthWaived(): Boolean {
        return false
    }

    override fun consumerTokenRequired(): Boolean {
        return false
    }
}