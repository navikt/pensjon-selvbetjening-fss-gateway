package no.nav.pensjon.selvbetjening.fssgw.tech.basicauth

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils.hasText
import java.util.*

/**
 * Validator of Basic authorization (username and password).
 */
@Component
class BasicAuthValidator(
    @Value("\${fg.sts.selfservice.username}") private val username: String,
    @Value("\${fg.sts.selfservice.password}") private val password: String) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun validate(basicAuthString: String): Boolean {
        if (!hasText(basicAuthString)) {
            log.debug("No text in basic auth string")
            return false
        }

        val credentials = String(Base64.getDecoder().decode(basicAuthString))
        val parts = credentials.split(":")
        val authorized = parts.size == 2 && parts[0] == username && parts[1] == password

        if (!authorized && log.isDebugEnabled) {
            logBadAuth(parts)
        }

        return authorized
    }

    private fun logBadAuth(credentialsParts: List<String>) {
        if (credentialsParts.size != 2) {
            log.debug("Unexpected number of parts in basic auth string: ${credentialsParts.size}")
            return
        }

        if (credentialsParts[0] != username) {
            log.debug("Wrong username in basic auth string: ${credentialsParts[0]}")
        }

        if (credentialsParts[1] != password) {
            log.debug("Wrong password in basic auth string") // No logging of password
        }
    }
}
