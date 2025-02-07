package no.nav.pensjon.selvbetjening.fssgw.esb

import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.EgressBodyAuthController
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class EsbController(
    ingressTokenValidator: JwsValidator,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    @Value("\${esb.url}") egressEndpoint: String,
    @Value("\${fg.sts.selfservice.password}") private val password: String)
    : EgressBodyAuthController(ingressTokenValidator, serviceClient, callIdGenerator, egressEndpoint, password) {

    private val logger = LoggerFactory.getLogger(EsbController::class.java)

    @PostMapping(
        value = [
            "nav-cons-pen-pselv-utbetalingWeb/sca/PSELVUtbetalingWSEXP", // sca = Service Component Architecture
            "nav-tjeneste-behandleTrekk_v1Web/sca/BehandleTrekkWSEXP",
            "nav-tjeneste-trekk_v1Web/sca/TrekkWSEXP"]
    )
    fun handlePostRequest(@RequestBody body: String, request: HttpServletRequest): ResponseEntity<String> {
        logger.info("Request url: ${request.requestURL}")
        if (request.requestURI.contains("TrekkWSEXP")) {
            logger.info(body)
        }
        return doPost(request, body)
    }
}
