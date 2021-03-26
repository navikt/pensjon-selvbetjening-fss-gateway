package no.nav.pensjon.selvbetjening.fssgw.tech.sts

import java.time.LocalDateTime

interface TimeProvider {

    fun time(): LocalDateTime
}
