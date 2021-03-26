package no.nav.pensjon.selvbetjening.fssgw.tech.sts

import java.time.LocalDateTime

data class ServiceTokenData(

        val accessToken: String,
        val tokenType: String,
        val issuedTime: LocalDateTime,
        val expiresInSeconds: Long
)
