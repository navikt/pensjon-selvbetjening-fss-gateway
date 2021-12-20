package no.nav.pensjon.selvbetjening.fssgw.dkif

import no.nav.pensjon.selvbetjening.fssgw.common.ControllerBase
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("api/v1")
class DkifController(
    jwsValidator: JwsValidator,
    egressTokenGetter: ServiceTokenGetter,
    serviceClient: ServiceClient,
    @Value("\${dkif.url}") egressEndpoint: String) :
    ControllerBase(jwsValidator, serviceClient, egressTokenGetter, egressEndpoint) {

    @GetMapping("personer/kontaktinformasjon")
    fun getKontaktinformasjon(request: HttpServletRequest): ResponseEntity<String> {
        return super.handle(request)
    }

    override fun egressAuthWaived(): Boolean {
        return false
    }
}
