package no.nav.pensjon.selvbetjening.fssgw.tech.sts

import java.time.LocalDateTime

interface ExpirationChecker {

    fun isExpired(issuedTime: LocalDateTime, expiresInSeconds: Long): Boolean

    fun time(): LocalDateTime
}
