package no.nav.pensjon.selvbetjening.fssgw.regler

import no.nav.pensjon.selvbetjening.fssgw.common.ProtectedControllerBase
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("api/hentGrunnbelopListe")
class PensjonReglerController(
    jwsValidator: JwsValidator,
    egressTokenGetter: ServiceTokenGetter,
    serviceClient: ServiceClient,
    @Value("\${pensjonregler.url}") egressEndpoint: String) :
    ProtectedControllerBase(jwsValidator, egressTokenGetter, serviceClient, egressEndpoint) {

    @PostMapping
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
