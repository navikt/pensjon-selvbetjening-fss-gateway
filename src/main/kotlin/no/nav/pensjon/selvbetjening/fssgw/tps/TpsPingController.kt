package no.nav.pensjon.selvbetjening.fssgw.tps

import no.nav.pensjon.selvbetjening.fssgw.common.BasicProtectedControllerBase
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.basicauth.BasicAuthValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

/**
 * tps = Tjenestebasert persondatasystem
 * ws = Web services
 * aura = Automatisk utrulling av applikasjoner
 */
@RestController
@RequestMapping("tpsws-aura/ws")
class TpsPingController(
    authValidator: BasicAuthValidator,
    serviceClient: ServiceClient,
    @Value("\${tps.person.url}") egressEndpoint: String) :
    BasicProtectedControllerBase(authValidator, serviceClient, egressEndpoint) {

    @PostMapping("Person/v3")
    fun handlePostRequest(@RequestBody body: String, request: HttpServletRequest): ResponseEntity<String> {
        return super.doPost(request, body)
    }
}
