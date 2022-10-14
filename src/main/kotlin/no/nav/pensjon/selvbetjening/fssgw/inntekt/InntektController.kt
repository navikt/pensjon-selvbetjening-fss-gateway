package no.nav.pensjon.selvbetjening.fssgw.inntekt

import no.nav.pensjon.selvbetjening.fssgw.common.ProtectedControllerBase
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

const val PATH = "api/v1/"

@RestController
@RequestMapping("inntektskomponenten-ws/rs")
class InntektController (
    jwsValidator: JwsValidator,
    egressTokenGetter: ServiceTokenGetter,
    serviceClient: ServiceClient,
    @Value("\${inntekt.url}") egressEndpoint: String) :
    ProtectedControllerBase(jwsValidator, egressTokenGetter, serviceClient, egressEndpoint) {

    @GetMapping("${PATH}forventetinntekt")
    fun handleGetRequest(request: HttpServletRequest): ResponseEntity<String> {
        return super.doGet(request)
    }

    @PostMapping(
        value = [
            "${PATH}forventetinntekt",
            "${PATH}hentdetaljerteabonnerteinntekter",
            "${PATH}hentabonnerteinntekterbolk"])
    fun handlePostRequest(@RequestBody body: String, request: HttpServletRequest): ResponseEntity<String> {
        return super.doPost(request, body)
    }

    override fun egressAuthWaived(): Boolean {
        return false
    }

    override fun consumerTokenRequired(): Boolean {
        return true
    }
}