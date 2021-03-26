package no.nav.pensjon.selvbetjening.fssgw.tech.sts

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class LeewayExpirationChecker(private val timeProvider: TimeProvider,
                              @Value("\${sts.token.expiration.leeway}") leewaySeconds: String)
    : ExpirationChecker {

    val leewaySeconds: Long = leewaySeconds.toLong()

    override fun isExpired(issuedTime: LocalDateTime, expiresInSeconds: Long): Boolean {
        val deadline = issuedTime.plusSeconds(expiresInSeconds - leewaySeconds)
        return timeProvider.time().isAfter(deadline)
    }

    override fun time(): LocalDateTime {
        return timeProvider.time()
    }
}
