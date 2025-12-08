package no.nav.pensjon.selvbetjening.fssgw.sts

import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.EgressHeaderBasicAuthController
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("rest/v1/sts")
class StsController(
    ingressTokenValidator: JwsValidator,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    @Value("\${sts.url}") egressEndpoint: String,
    @Value("\${fg.sts.tp.username}") serviceUsername3: String,
    @Value("\${fg.sts.tp.password}") servicePassword3: String
) : EgressHeaderBasicAuthController(
    ingressTokenValidator,
    serviceClient,
    callIdGenerator,
    egressEndpoint,
    mapOf(
        3 to "$serviceUsername3:$servicePassword3"
    )
) {
    @GetMapping("token")
    fun handleGetRequest(request: HttpServletRequest) = super.doGet(request)

    @PostMapping("token/exchange")
    fun handlePostRequest(@RequestBody body: String, request: HttpServletRequest) =
        doPost(request, body,
            serviceUserId = request.getHeader("Service-User-Id")?.toIntOrNull() ?: DEFAULT_SERVICE_USER_ID
        )

    private companion object {
        private const val DEFAULT_SERVICE_USER_ID = 3 // srvtjenestepensjon
    }
}
