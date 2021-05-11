package no.nav.pensjon.selvbetjening.fssgw.tech.oauth2

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

/**
 * Support for handling multiple token issuers in OAuth2 flows.
 */
@Component
class MultiIssuerSupport(@Qualifier("\${external-user}") private val externalUserOauth2BasicData: Oauth2BasicData,
                         @Qualifier("\${internal-user}") private val internalUserOauth2BasicData: Oauth2BasicData) {

    private val handlersByIssuer: MutableMap<String, Oauth2Handler> = HashMap()
    private val oauth2Basics: List<Oauth2BasicData> = listOf(externalUserOauth2BasicData, internalUserOauth2BasicData)
    private val log = LoggerFactory.getLogger(javaClass)

    fun getOauth2HandlerForIssuer(issuer: String): Oauth2Handler {
        return handlersByIssuer.computeIfAbsent(issuer) { freshOauth2Handler(issuer) }
    }

    private fun freshOauth2Handler(issuer: String): Oauth2Handler {
        var index = 0
        var found: Boolean
        var configGetter: Oauth2ConfigGetter

        do {
            configGetter = WebOauth2ConfigGetter(oauth2Basics[index].wellKnownUrl)
            found = configGetter.getIssuer() == issuer
        } while (!found && ++index < oauth2Basics.size)

        if (!found) {
            val message = "Invalid issuer '$issuer'"
            log.error(message)
            throw Oauth2Exception(message)
        }

        return Oauth2Handler(
                configGetter,
                Oauth2KeyDataGetter(configGetter),
                oauth2Basics[index].acceptedAudience)
    }
}
