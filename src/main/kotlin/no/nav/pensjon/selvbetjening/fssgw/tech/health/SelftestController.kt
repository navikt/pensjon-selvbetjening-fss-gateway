package no.nav.pensjon.selvbetjening.fssgw.tech.health

import no.nav.pensjon.selvbetjening.fssgw.tech.oauth2.WebOauth2ConfigGetter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.util.StringUtils.hasLength
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal")
class SelftestController(
        @Value("\${external-user.oauth2.well-known-url}") private val externalUserWellKnownUrl: String,
        @Value("\${internal-user.oauth2.well-known-url}") private val internalUserWellKnownUrl: String) {

    val log: Logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("selftest")
    fun selftest(): String {
        log.debug("Performing selftest")
        return "${testExternalUserOauth2WellKnownUrl()} | ${testInternalUserOauth2WellKnownUrl()}"
    }

    private fun testExternalUserOauth2WellKnownUrl(): String {
        log.debug("Testing OAuth2 well-known URL for external users")
        val issuer = WebOauth2ConfigGetter(externalUserWellKnownUrl).getIssuer()
        val status = if (hasLength(issuer)) "up" else "down"
        return "TokenDings: $status"
    }

    private fun testInternalUserOauth2WellKnownUrl(): String {
        log.debug("Testing OAuth2 well-known URL for internal users")
        val issuer = WebOauth2ConfigGetter(internalUserWellKnownUrl).getIssuer()
        val status = if (hasLength(issuer)) "up" else "down"
        return "Azure AD: $status"
    }
}
