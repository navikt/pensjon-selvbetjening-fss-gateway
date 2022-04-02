package no.nav.pensjon.selvbetjening.fssgw.mock

import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenData
import java.time.LocalDateTime

object MockUtil {

    fun serviceTokenData(): ServiceTokenData = ServiceTokenData(
        "j.w.t",
        "JWT", LocalDateTime.now(),
        60L)
}
