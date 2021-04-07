package no.nav.pensjon.selvbetjening.fssgw.tech.health

import no.nav.pensjon.selvbetjening.fssgw.tech.oidc.OidcConfigGetter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal")
class SelftestController(val oidcConfigGetter: OidcConfigGetter) {

    val log: Logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("selftest")
    fun selftest(): String {
        log.debug("Performing selftest")
        val uri = oidcConfigGetter.getJsonWebKeySetUri()
        val status = if (uri.startsWith("http")) "up" else "down"
        return "Tokendings: $status"
    }
}
