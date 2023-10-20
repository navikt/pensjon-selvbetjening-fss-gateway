package no.nav.pensjon.selvbetjening.fssgw.person.ws

import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.EgressBodyAuthController
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@RestController
@RequestMapping("tpsws-aura")
class PersonController(
    ingressTokenValidator: JwsValidator,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    @Value("\${app.url}") egressEndpoint: String,
    @Value("\${sts.password}") private val password: String)
    : EgressBodyAuthController(ingressTokenValidator, serviceClient, callIdGenerator, egressEndpoint, password) {

    private val log: Logger = LoggerFactory.getLogger(FamilierelasjonService::class.java)

    @PostMapping("ws/Person/v3")
    fun handlePostRequest(@RequestBody body: String, request: HttpServletRequest): ResponseEntity<String> {
        logger.info("Gjør proxykall til tpsws-aura")
        return super.doPost(request, body)
    }


}
